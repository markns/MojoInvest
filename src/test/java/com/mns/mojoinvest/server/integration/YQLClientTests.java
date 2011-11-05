package com.mns.mojoinvest.server.integration;

import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.servlet.HistoricQuoteLoaderServlet;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class YQLClientTests {

    private final QuoteDao quoteDao = new QuoteDao(ObjectifyService.factory());
    private final FundDao fundDao = new FundDao(ObjectifyService.factory());

    private final HistoricQuoteLoaderServlet historicQuoteLoaderServlet =
            new HistoricQuoteLoaderServlet(fundDao, quoteDao);

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


}
