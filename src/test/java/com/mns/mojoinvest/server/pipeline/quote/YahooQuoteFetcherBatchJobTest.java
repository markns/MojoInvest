package com.mns.mojoinvest.server.pipeline.quote;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.ObjectifyQuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class YahooQuoteFetcherBatchJobTest {

    private final QuoteDao quoteDao = new ObjectifyQuoteDao(ObjectifyService.factory());

    private final LocalDatastoreServiceTestConfig config = new LocalDatastoreServiceTestConfig();
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(config);

    @Before
    public void setUp() throws Exception {
        helper.setUp();

        List<Quote> quotes = new ArrayList<Quote>(10);
        quotes.add(fromStrArr("SPY", new String[]{"2012-03-22", "139.18", "139.55", "138.74", "139.20", "135133800", "139.20"}));
        quotes.add(fromStrArr("SPY", new String[]{"2012-03-21", "140.52", "140.65", "139.92", "140.21", "122388400", "140.21"}));
        quotes.add(fromStrArr("SPY", new String[]{"2012-03-20", "140.05", "140.61", "139.64", "140.44", "121729700", "140.44"}));
        quotes.add(fromStrArr("SPY", new String[]{"2012-03-19", "140.21", "141.28", "140.11", "140.85", "125291100", "140.85"}));
        quotes.add(fromStrArr("SPY", new String[]{"2012-03-16", "140.36", "140.48", "140.00", "140.30", "152893500", "140.30"}));

        quotes.add(fromStrArr("QQQ", new String[]{"2012-03-22", "66.72", "67.14", "66.68", "66.98", "56953900", "66.98"}));
        quotes.add(fromStrArr("QQQ", new String[]{"2012-03-21", "67.12", "67.49", "67.03", "67.12", "39648100", "67.12"}));
        quotes.add(fromStrArr("QQQ", new String[]{"2012-03-20", "66.64", "67.18", "66.46", "67.11", "47290600", "67.11"}));
        quotes.add(fromStrArr("QQQ", new String[]{"2012-03-19", "66.63", "67.15", "66.45", "66.99", "60966200", "66.99"}));
        quotes.add(fromStrArr("QQQ", new String[]{"2012-03-16", "66.67", "66.67", "66.36", "66.52", "5302300", "66.52"}));

        quoteDao.put(quotes);
    }

    @Test
    public void testRun() throws Exception {
        YahooQuoteFetcherBatchJob job = new YahooQuoteFetcherBatchJob();
        List<Fund> funds = new ArrayList<Fund>(2);
        funds.add(new Fund("SPY", "", "", "", true, "", "", "", new LocalDate("1993-03-03")));
        funds.add(new Fund("QQQ", "", "", "", true, "", "", "", new LocalDate("1993-03-03")));

        job.run(funds, new LocalDate("2012-03-23"));
    }

    @Test
    public void testSymbolNotAvailable() throws Exception {
        YahooQuoteFetcherBatchJob job = new YahooQuoteFetcherBatchJob();
        List<Fund> funds = new ArrayList<Fund>(2);
        funds.add(new Fund("BARL", "", "", "", true, "", "", "", new LocalDate("1993-03-03")));

        job.run(funds, new LocalDate("2012-03-23"));
    }


    private static Quote fromStrArr(String symbol, String[] row) {
        return new Quote(symbol,
                new LocalDate(row[0]),
                row[1].isEmpty() ? null : new BigDecimal(row[1]),
                row[2].isEmpty() ? null : new BigDecimal(row[2]),
                row[3].isEmpty() ? null : new BigDecimal(row[3]),
                row[4].isEmpty() ? null : new BigDecimal(row[4]),
                null, null,
                row[5].isEmpty() ? null : new BigDecimal(row[5]),
                row[6].isEmpty() ? null : new BigDecimal(row[6]),
                false);
    }
}
