package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.yql.JavascriptType;
import com.mns.mojoinvest.server.yql.QueryType;
import com.mns.mojoinvest.server.yql.QuoteType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.joda.time.LocalDate;

import javax.ws.rs.core.MultivaluedMap;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class QuoteFetcherJob extends Job1<List<Quote>, List<String>> {

    private static final Logger log = Logger.getLogger(QuoteFetcherJob.class.getName());

    private static final String yqlGet = "http://query.yahooapis.com/v1/public/yql";

    public static final String DATATABLES = "store://datatables.org/alltableswithkeys";

    @Override
    public Value<List<Quote>> run(List<String> symbols) {
        return immediate(getQuotes(symbols));
    }

    public List<Quote> getQuotes(List<String> symbols) {
        if (symbols == null)
            throw new IllegalArgumentException("Symbols cannot be null");

        Client c = Client.create();
        WebResource r = c.resource(yqlGet);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        Joiner joiner = Joiner.on("\",\"");
        String symbolStr = "\"" + joiner.join(symbols) + "\"";
        log.info("Retrieving quotes for " + symbolStr);
        params.add("q", "select * from yahoo.finance.quotes where symbol in (" + symbolStr + ")");
        params.add("env", DATATABLES);
        params.add("diagnostics", "true");

        QueryType query = r.queryParams(params)
                .accept("application/xml")
                .get(QueryType.class);

        validateResponse(params, query);

        return parseQuotes(query);
    }

    @VisibleForTesting
    protected List<Quote> parseQuotes(QueryType query) {
        List<Quote> quotes = new ArrayList<Quote>();
        if (query.getResults() != null && query.getResults().getQuote() != null) {
            for (QuoteType qt : query.getResults().getQuote()) {
                Quote quote = parseQuoteType(qt);
                if (quote != null) {
                    quotes.add(quote);
                }
            }
        }
        return quotes;
    }

    private Quote parseQuoteType(QuoteType qt) {

        String symbol = qt.getSymbol();
        if (symbol == null || symbol.isEmpty())
            throw new IllegalStateException("Symbol was null or empty for quote type " + qt);

        //TODO: Should this localdate be specified explicitly. What is the timezone?
        LocalDate date = new LocalDate();

        BigDecimal open = newBigDecimalIfValid(qt.getOpen());
        BigDecimal high = newBigDecimalIfValid(qt.getDaysHigh());
        BigDecimal low = newBigDecimalIfValid(qt.getDaysLow());
        BigDecimal close = null;

        BigDecimal bid;
        BigDecimal ask;
        try {
            bid = new BigDecimal(qt.getBidRealtime());
            ask = new BigDecimal(qt.getAskRealtime());
        } catch (NumberFormatException e) {
            log.warning("Unable to parse bid or ask for " + symbol + " on " + date +
                    ". Bid: " + qt.getBidRealtime() + ", Ask: " + qt.getAskRealtime());
            return null;
        }
        if (bid.compareTo(BigDecimal.ZERO) == 0) {
            log.warning("Bid for " + symbol + " on " + date + " was zero");
            return null;
        }
        if (ask.compareTo(BigDecimal.ZERO) == 0) {
            log.warning("Ask for " + symbol + " on " + date + " was zero");
            return null;
        }
        if (ask.subtract(bid).compareTo(BigDecimal.ZERO) < 0) {
            log.warning("Bid was greater than ask for " + symbol + " on " + date +
                    ". Bid: " + bid + ", Ask: " + ask);
            return null;
        }
        if (ask.divide(bid, RoundingMode.HALF_EVEN).compareTo(BigDecimal.TEN) > 0) {
            log.warning("Ask was more than 10x bid for " + symbol + " on " + date +
                    ". Bid: " + bid + ", Ask: " + ask);
            return null;
        }

        BigDecimal volume = newBigDecimalIfValid(qt.getVolume());
        BigDecimal adjClose = null;

        boolean rolled = false;

        return new Quote(symbol, date, open, high, low, close, bid, ask, volume, adjClose, rolled);
    }

    protected BigDecimal newBigDecimalIfValid(String str) {
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            return null;
        }
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

}
