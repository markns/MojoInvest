package com.mns.alphaposition.server.pipeline;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Singleton;
import com.mns.alphaposition.server.engine.model.Quote;
import com.mns.alphaposition.server.yql.JavascriptType;
import com.mns.alphaposition.server.yql.QueryType;
import com.mns.alphaposition.server.yql.QuoteType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.joda.time.LocalDate;

import javax.ws.rs.core.MultivaluedMap;
import java.math.BigDecimal;
import java.util.logging.Logger;

@Singleton
public class QuoteFetcherJob extends Job1<Quote, String> {

    private static final Logger log = Logger.getLogger(QuoteFetcherJob.class.getName());

    private static final String yqlGet = "http://query.yahooapis.com/v1/public/yql";

    public static final String DATATABLES = "store://datatables.org/alltableswithkeys";

    @Override
    public Value<Quote> run(String symbol) {
        return immediate(getRealtimeQuote(symbol));
    }

    public Quote getRealtimeQuote(String symbol) {
        if (symbol == null)
            throw new IllegalArgumentException("Symbol cannot be null");

        Client c = Client.create();
        WebResource r = c.resource(yqlGet);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        params.add("q", "select * from yahoo.finance.quotes where symbol in (\"" + symbol + "\")");
        params.add("env", DATATABLES);
        params.add("diagnostics", "true");

        QueryType query = r.queryParams(params)
                .accept("application/xml")
                .get(QueryType.class);

        validateResponse(params, query);

        return parseQuote(query, symbol);
    }

    private Quote parseQuote(QueryType query, String symbol) {
        if (query.getResults() != null && query.getResults().getQuote() != null
                && query.getResults().getQuote().get(0) != null) {
            QuoteType qt = query.getResults().getQuote().get(0);
            Quote quote = new Quote(qt.getSymbol(),
                    new LocalDate(),
                    new BigDecimal(qt.getOpen()),
                    new BigDecimal(qt.getDaysHigh()),
                    new BigDecimal(qt.getDaysLow()),
                    null,
                    new BigDecimal(qt.getBidRealtime()),
                    new BigDecimal(qt.getAskRealtime()),
                    new BigDecimal(qt.getVolume()),
                    null,
                    false);
            log.fine("Retrieved quote " + quote);
            return quote;
        }
        log.warning("Failed to retrieve quote for '" + symbol + "' on " + new LocalDate());
        return null;
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
