package com.mns.alphaposition.server.integration;

import com.googlecode.objectify.ObjectifyService;
import com.mns.alphaposition.server.engine.model.FundDao;
import com.mns.alphaposition.server.engine.model.Quote;
import com.mns.alphaposition.server.engine.model.QuoteDao;
import com.mns.alphaposition.server.pipeline.QuoteFetcherJob;
import com.mns.alphaposition.server.servlet.HistoricQuoteLoaderServlet;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class YQLClientTests {

    private final QuoteDao quoteDao = new QuoteDao(ObjectifyService.factory());
    private final FundDao fundDao = new FundDao(ObjectifyService.factory());

    private final HistoricQuoteLoaderServlet historicQuoteLoaderServlet =
            new HistoricQuoteLoaderServlet(fundDao, quoteDao);

    private final QuoteFetcherJob job = new QuoteFetcherJob();

    @Test
    public void testLocalDateFormatter() {
        LocalDate startDate = new LocalDate(2011, 5, 11);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        System.out.println(fmt.print(startDate));
    }

    @Test
    public void testYqlHistoricalDataGet() {

        //TODO: This is an integration test - how to run separately??
        List<Quote> quotes = historicQuoteLoaderServlet.getHistoricQuotes("GOOG",
                new LocalDate("2009-03-08"), new LocalDate("2010-03-10"));

        //Could fail if number of quotes changes, but it shouldn't.
        assertEquals(254, quotes.size());
    }

    @Test
    public void testYqlRealtimeDataGet() {

        List<String> symbols = Arrays.asList("BHH");
        List<Quote> quotes = job.getQuotes(symbols);
        assertNull(quotes.get(0).getClose());

    }


}
