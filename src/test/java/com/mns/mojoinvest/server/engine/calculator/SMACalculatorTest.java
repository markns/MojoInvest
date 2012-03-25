package com.mns.mojoinvest.server.engine.calculator;

import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.InMemoryFundDao;
import org.junit.Before;
import org.junit.Test;

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
    private final CalculatedValueDao calculatedValueDao = new CalculatedValueDao(ObjectifyService.factory());


}
