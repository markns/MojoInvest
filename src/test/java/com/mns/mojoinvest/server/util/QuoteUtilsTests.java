package com.mns.mojoinvest.server.util;

import com.mns.mojoinvest.server.engine.model.Quote;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class QuoteUtilsTests {


    @Test
    public void testFill() {

        List<Quote> quotes = new ArrayList<Quote>();

        quotes.add(new Quote("EWA", new LocalDate(2011, 7, 7), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        quotes.add(new Quote("EWA", new LocalDate(2011, 7, 11), new BigDecimal("2"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        quotes.add(new Quote("EWA", new LocalDate(2011, 7, 13), new BigDecimal("3"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        quotes.add(new Quote("EWA", new LocalDate(2011, 7, 14), new BigDecimal("4"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        quotes.add(new Quote("EWA", new LocalDate(2011, 7, 18), new BigDecimal("5"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));

        List<Quote> expected = new ArrayList<Quote>();

        expected.add(new Quote("EWA", new LocalDate(2011, 7, 4), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), true));
        expected.add(new Quote("EWA", new LocalDate(2011, 7, 5), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), true));
        expected.add(new Quote("EWA", new LocalDate(2011, 7, 6), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), true));
        expected.add(new Quote("EWA", new LocalDate(2011, 7, 8), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), true));
        expected.add(new Quote("EWA", new LocalDate(2011, 7, 12), new BigDecimal("2"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), true));
        expected.add(new Quote("EWA", new LocalDate(2011, 7, 15), new BigDecimal("4"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), true));
        expected.add(new Quote("EWA", new LocalDate(2011, 7, 19), new BigDecimal("5"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), true));
        expected.add(new Quote("EWA", new LocalDate(2011, 7, 20), new BigDecimal("5"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), true));

        List<Quote> actual = QuoteUtils.getMissingQuotes(new LocalDate(2011, 7, 4), new LocalDate(2011, 7, 20), quotes);

        assertEquals(expected, actual);
    }

    @Test
    public void testRollMissing() {

        List<Quote> quotes = new ArrayList<Quote>();

        quotes.add(new Quote("EWA", new LocalDate(2011, 7, 7), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        quotes.add(new Quote("EWA", new LocalDate(2011, 7, 11), new BigDecimal("2"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        quotes.add(new Quote("EWA", new LocalDate(2011, 7, 13), new BigDecimal("3"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        quotes.add(new Quote("EWA", new LocalDate(2011, 7, 14), new BigDecimal("4"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        quotes.add(new Quote("EWA", new LocalDate(2011, 7, 18), new BigDecimal("5"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));

        List<Quote> expected = new ArrayList<Quote>();

        expected.add(new Quote("EWA", new LocalDate(2011, 7, 8), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), true));
        expected.add(new Quote("EWA", new LocalDate(2011, 7, 12), new BigDecimal("2"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), true));
        expected.add(new Quote("EWA", new LocalDate(2011, 7, 15), new BigDecimal("4"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), true));

        List<Quote> actual = QuoteUtils.rollMissingQuotes(quotes);

        assertEquals(expected, actual);
    }


    @Test
    public void testFill2() {

        List<Quote> quotes = new ArrayList<Quote>();

        quotes.add(new Quote("XPP", new LocalDate("2009-06-04"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        quotes.add(new Quote("XPP", new LocalDate("2009-06-05"), new BigDecimal("2"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        quotes.add(new Quote("XPP", new LocalDate("2009-06-08"), new BigDecimal("3"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));

        List<Quote> expected = new ArrayList<Quote>();

        expected.add(new Quote("XPP", new LocalDate("2009-06-09"), new BigDecimal("3"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), true));

        List<Quote> actual = QuoteUtils.getMissingQuotes(new LocalDate("2009-06-04"), new LocalDate("2009-06-09"), quotes);

        assertEquals(expected, actual);
    }



    @Test
    public void testFillQuotesExtendBeforeFromDate() {

        List<Quote> quotes = new ArrayList<Quote>();

        quotes.add(new Quote("XPP", new LocalDate("2009-06-04"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        quotes.add(new Quote("XPP", new LocalDate("2009-06-05"), new BigDecimal("2"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        quotes.add(new Quote("XPP", new LocalDate("2009-06-08"), new BigDecimal("3"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        quotes.add(new Quote("XPP", new LocalDate("2009-06-10"), new BigDecimal("4"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));

        List<Quote> expected = new ArrayList<Quote>();

        expected.add(new Quote("XPP", new LocalDate("2009-06-09"), new BigDecimal("3"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), true));
        expected.add(new Quote("XPP", new LocalDate("2009-06-11"), new BigDecimal("4"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), true));

        List<Quote> actual = QuoteUtils.getMissingQuotes(new LocalDate("2009-06-09"), new LocalDate("2009-06-11"), quotes);

        assertEquals(expected, actual);
    }


}
