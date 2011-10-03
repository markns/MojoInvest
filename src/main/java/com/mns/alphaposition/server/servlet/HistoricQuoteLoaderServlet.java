package com.mns.alphaposition.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.alphaposition.server.engine.model.Fund;
import com.mns.alphaposition.server.engine.model.FundDao;
import com.mns.alphaposition.server.engine.model.Quote;
import com.mns.alphaposition.server.engine.model.QuoteDao;
import com.mns.alphaposition.server.yql.JavascriptType;
import com.mns.alphaposition.server.yql.QueryType;
import com.mns.alphaposition.server.yql.QuoteType;
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

@Singleton
public class HistoricQuoteLoaderServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(HistoricQuoteLoaderServlet.class.getName());

    private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");

    private static final String yqlGet = "http://query.yahooapis.com/v1/public/yql";

    public static final String DATATABLES = "http://datatables.org/alltables.env";

    public static final String SYMBOL = "symbol";
    public static final String START = "start";
    public static final String END = "end";

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

        LocalDate startDate = fmt.parseDateTime(req.getParameter(START)).toLocalDate();
        LocalDate endDate =  fmt.parseDateTime(req.getParameter(END)).toLocalDate();

        List<Quote> quotes = new ArrayList<Quote>();
        if (req.getParameter(SYMBOL) != null) {
            log.info("Loading quotes for " + req.getParameter(SYMBOL) + " between " + startDate + " and " + endDate);
            quotes.addAll(getHistoricQuotes(req.getParameter(SYMBOL), startDate, endDate));
        } else {
            List<Fund> funds = fundDao.list();
            log.info("Loading quotes for " + funds.size() + " funds between " + startDate + " and " + endDate);
            for (Fund fund : funds) {
                quotes.addAll(getHistoricQuotes(fund.getSymbol(), startDate, endDate));
            }
        }

        quoteDao.put(quotes);
    }

    public List<Quote> getHistoricQuotes(final String symbol, LocalDate startDate, LocalDate endDate) {

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
                        new LocalDate(fmt.parseDateTime(qt.getDate())),
                        new BigDecimal(qt.getOpen()),
                        new BigDecimal(qt.getHigh()),
                        new BigDecimal(qt.getLow()),
                        new BigDecimal(qt.getClose()),
                        new BigDecimal(qt.getVolume()),
                        new BigDecimal(qt.getAdjClose()),
                        false));
            }
        }
        return quotes;
    }

}
