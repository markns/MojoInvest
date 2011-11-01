package com.mns.mojoinvest.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.FundDao;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.QuoteDao;
import com.mns.mojoinvest.server.yql.JavascriptType;
import com.mns.mojoinvest.server.yql.QueryType;
import com.mns.mojoinvest.server.yql.QuoteType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class HistoricQuoteLoaderServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(HistoricQuoteLoaderServlet.class.getName());

    private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

    private static final String yqlGet = "http://query.yahooapis.com/v1/public/yql";

    public static final String DATATABLES = "store://datatables.org/alltableswithkeys";

    public static final String SYMBOL = "symbol";
    public static final String START = "start";
    public static final String END = "end";
    public static final String RANGE = "range";

    Pattern p = Pattern.compile("^([0-9]+)([a-zA-Z])");

    private final FundDao fundDao;
    private final QuoteDao quoteDao;

    @Inject
    public HistoricQuoteLoaderServlet(FundDao fundDao, QuoteDao quoteDao) {
        this.fundDao = fundDao;
        this.quoteDao = quoteDao;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        DateRange dateRange = new DateRange(req).parse();
        LocalDate startDate = dateRange.getStartDate();
        LocalDate endDate = dateRange.getEndDate();
        List<String> symbols = parseSymbols(req);

        for (String symbol : symbols) {
            List<Quote> quotes = getHistoricQuotes(symbol, startDate, endDate);
            quoteDao.put(quotes);
        }
    }

    private List<String> parseSymbols(HttpServletRequest req) {
        List<String> symbols = new ArrayList<String>();
        if (req.getParameter(SYMBOL) != null) {
            symbols.add(req.getParameter(SYMBOL));
        } else {
            List<Fund> funds = fundDao.list();
            for (Fund fund : funds) {
                symbols.add(fund.getSymbol());
            }
        }
        return symbols;
    }

    public List<Quote> getHistoricQuotes(List<String> symbols, LocalDate startDate, LocalDate endDate) {
        if (symbols.size() == 1) {
            log.info("Loading quotes for " + symbols.get(0) + " between " + startDate + " and " + endDate);
        } else {
            log.info("Loading quotes for " + symbols.size() + " funds between " + startDate + " and " + endDate);
        }

        List<Quote> quotes = new ArrayList<Quote>();
        for (String symbol : symbols) {
            quotes.addAll(getHistoricQuotes(symbol, startDate, endDate));
        }
        return quotes;
    }

    public List<Quote> getHistoricQuotes(final String symbol, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null)
            throw new IllegalArgumentException("Start date and end date cannot be null");
        if (endDate.isBefore(startDate))
            throw new IllegalArgumentException("End date cannot be before start date");
        if (symbol == null)
            throw new IllegalArgumentException("Symbol cannot be null");

        Client c = Client.create();
        WebResource r = c.resource(yqlGet);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        params.add("q", "select * from yahoo.finance.historicaldata where symbol = \"" + symbol + "\" " +
                "and startDate = \"" + startDate + "\" " +
                "and endDate = \"" + endDate + "\"");
        params.add("env", DATATABLES);
        params.add("diagnostics", "true");

        QueryType query = r.queryParams(params)
                .accept("application/xml")
                .get(QueryType.class);

        validateResponse(params, query);

        return parseQuotes(symbol, query);
    }

    private void validateResponse(MultivaluedMap<String, String> params, QueryType query) {
        //Check the datatable is available.
        for (JavascriptType javascript : query.getDiagnostics().getJavascript()) {
            if (javascript.getValue().contains("blocked")) {
                //TODO: Should throw a checked exception here
                throw new RuntimeException("Unable to continue processing with "
                        + params + ". " + javascript);
            }
        }
    }

    private List<Quote> parseQuotes(String symbol, QueryType query) {
        final List<Quote> quotes = new ArrayList<Quote>();
        if (query.getResults() != null && query.getResults().getQuote() != null) {

            for (QuoteType qt : query.getResults().getQuote()) {
                //OldQuote XML returned from YQL doesn't contain symbol, so set it here
                quotes.add(new Quote(symbol,
                        new LocalDate(qt.getDate()),
                        new BigDecimal(qt.getOpen()),
                        new BigDecimal(qt.getHigh()),
                        new BigDecimal(qt.getLow()),
                        new BigDecimal(qt.getClose()),
                        //bid, ask
                        null, null,
                        new BigDecimal(qt.getVolume()),
                        new BigDecimal(qt.getAdjClose()),
                        false));
            }
        }
        return quotes;
    }

    private class DateRange {
        private HttpServletRequest req;
        private LocalDate startDate;
        private LocalDate endDate;

        public DateRange(HttpServletRequest req) {
            this.req = req;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public DateRange parse() {
            if (req.getParameter(RANGE) != null) {
                endDate = new LocalDate();
                startDate = parseDateFromRange(endDate, req.getParameter(RANGE));
            } else if (req.getParameter(START) != null && req.getParameter(END) != null) {
                startDate = fmt.parseDateTime(req.getParameter(START)).toLocalDate();
                endDate = fmt.parseDateTime(req.getParameter(END)).toLocalDate();
            } else {
                throw new IllegalArgumentException("Either date range, or start and end dates, must be specified");
            }
            return this;
        }

        private LocalDate parseDateFromRange(LocalDate endDate, String range) {
            Matcher m = p.matcher(range);
            if (m.find()) {
                int num = Integer.parseInt(m.group(1));
                String unit = m.group(2);
                if (unit.toUpperCase().equals("D")) {
                    return endDate.minusDays(num);
                } else if (unit.toUpperCase().equals("W")) {
                    return endDate.minusWeeks(num);
                } else if (unit.toUpperCase().equals("M")) {
                    return endDate.minusMonths(num);
                } else if (unit.toUpperCase().equals("Y")) {
                    return endDate.minusYears(num);
                } else {
                    throw new IllegalArgumentException("Unable to parse unit '" + unit + "' to determine date range.");
                }
            }
            throw new IllegalArgumentException("Unable to parse '" + range + "' to determine date range.");
        }
    }
}
