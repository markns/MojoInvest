package com.mns.mojoinvest.server.engine.calculator;

import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.InMemoryFundDao;
import com.mns.mojoinvest.server.engine.model.dao.InMemoryQuoteDao;
import com.mns.mojoinvest.server.util.QuoteUtils;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SMACalculatorTest {

    @Before
    public void setUp() throws Exception {

    }

    /*Use cases

    calculation of full SMA history - offline
    calculation of full SMA history - online

    calculation of new SMA during pipeline job

    assumptions:
        use weekly prices

    offline job - retrieve all quotes
                - make InMemoryQuoteDao
                - scan back through full
        | * | * | * | * | * | * | * | * | * | * | * | * | * | * | * | * | * | * | * | * | * |
          M               F   M           T   x   M               F   M               F

    mondays = getWeeklySeries(starting Monday)
    Quotes = getQuotes(symbol, mondays)
    SMACalculator sma = new SMACalculator(window)
    for (Quote quote : Quotes)
        sma.add(quote)
        BigDecimal avg = sma.getAverage()
        if (avg != null)
            String key = quote.getDate + | + symbol + | "SMA" | window
            CalculatedValue cv = new CalculatedValue(key, avg)
            dao.put(cv)

    online job - get X weeks series leading to T
               - add all to the SMA calculator
               - get average
               - store value
    */


    @Test
    public void testNewNum() throws Exception {

    }

    @Test
    public void testGetAvg() throws Exception {

    }


    private final InMemoryFundDao fundDao = new InMemoryFundDao();
    private final InMemoryQuoteDao quoteDao =
            new InMemoryQuoteDao(Arrays.asList("../ETFData/data/etf_sector_quotes.csv",
                    "../ETFData/data/yahoo_quotes_missing.csv"));
    private final CalculatedValueDao calculatedValueDao = new CalculatedValueDao(ObjectifyService.factory());


    @Test
    public void blah() {

        CalculationService service = new CalculationService(fundDao, quoteDao);
        Collection<Fund> funds = fundDao.getAll();

        for (Fund fund : funds) {

            List<Quote> quotes = quoteDao.get(fund);
            QuoteUtils.sortByDateAsc(quotes);
            LocalDate earliest = quotes.get(0).getDate();
            LocalDate latest = quotes.get(quotes.size() - 1).getDate();

            System.out.println(fund + " " + earliest + " " + latest);

            int period = 4;

            System.out.println("*****");
            service.calculateSMA(fund, earliest, latest, period);
            System.out.println("*****");
            service.calculateSMA(fund, earliest, latest.minusDays(1), period);
            System.out.println("*****");
            service.calculateSMA(fund, earliest, latest.minusDays(2), period);
            System.out.println("*****");
            service.calculateSMA(fund, earliest, latest.minusDays(3), period);
            System.out.println("*****");
            service.calculateSMA(fund, earliest, latest.minusDays(4), period);

            break;
        }


    }

}
