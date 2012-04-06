package com.mns.mojoinvest.server.engine.calculator;

import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Quote;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class CalculationServiceTest {
    @Test
    public void testCalculateSMA() throws Exception {

    }

    @Test
    public void testCalculateStandardDeviation() throws Exception {

    }

    @Test
    public void testCalculateROC() throws Exception {

        List<Quote> quotes = new ArrayList<Quote>();
        quotes.add(new Quote("A", new LocalDate(2011, 1, 7), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, null, null, BigDecimal.ZERO, new BigDecimal("1"), false));
        quotes.add(new Quote("A", new LocalDate(2011, 1, 14), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, null, null, BigDecimal.ZERO, new BigDecimal("1"), false));
        quotes.add(new Quote("A", new LocalDate(2011, 1, 21), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, null, null, BigDecimal.ZERO, new BigDecimal("3"), false));
        quotes.add(new Quote("A", new LocalDate(2011, 1, 28), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, null, null, BigDecimal.ZERO, new BigDecimal("4"), false));
        quotes.add(new Quote("A", new LocalDate(2011, 2, 4), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, null, null, BigDecimal.ZERO, new BigDecimal("5"), false));

        CalculationService service = new CalculationService();
        List<CalculatedValue> values = service.calculateROC(quotes, 2);

        List<CalculatedValue> expected = Arrays.asList(new CalculatedValue(new LocalDate(2011, 2, 4), "A", "ROC", 2, 66.667),
                new CalculatedValue(new LocalDate(2011, 1, 28), "A", "ROC", 2, 300),
                new CalculatedValue(new LocalDate(2011, 1, 21), "A", "ROC", 2, 200));
        assertEquals(expected, values);

    }

    @Test
    public void testCalculateRSquared() throws Exception {

    }
}
