package com.mns.mojoinvest.server.pipeline.quote;

import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class YahooQuoteFetcherTest {


    @Test
    public void testRun() throws QuoteFetcherException {
        Fund fund = new Fund("SPY", "", "", "", true, "", "", "", new LocalDate("1993-03-03"));
        YahooQuoteFetcher job = new YahooQuoteFetcher();
        List<Quote> quotes = job.run(fund, new LocalDate("2012-03-16"), new LocalDate("2012-03-22"));
        assertEquals(5, quotes.size());

    }

}
