package com.mns.mojoinvest.server;

import au.com.bytecode.opencsv.CSVWriter;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.transaction.AbstractTransaction;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.io.StringWriter;
import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;

public class CSVWriterTests {


    @Test
    public void testQuoteToStringArr() {
        Quote quote = new Quote("TEST", new LocalDate("2011-01-01"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false);

        StringWriter stringWriter = new StringWriter();
        CSVWriter writer = new CSVWriter(stringWriter);

        writer.writeNext(quote.toStrArr());

        String string = stringWriter.toString();
        assertEquals("\"TEST\",\"2011-01-01\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"false\"", string.trim());
    }

    @Test
    public void testQuoteFromStringArr() {
        Quote expected = new Quote("TEST", new LocalDate("2011-01-01"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false);
        String[] arr = new String[]{"TEST", "2011-01-01", "0", "0", "0", "0", "0", "0", "0", "0", "false"};
        Quote quote = Quote.fromStrArr(arr);
        assertEquals(expected, quote);
    }

    @Test
    public void testFundToStringArr() {

        Fund fund = new Fund("ABC", "ABC fund", "Category", "Provider", true,
                "US", "Index", "Blah blah", new LocalDate("2011-01-01"));

        StringWriter stringWriter = new StringWriter();
        CSVWriter writer = new CSVWriter(stringWriter);

        writer.writeNext(fund.toStrArr());

        String string = stringWriter.toString();
        assertEquals("\"ABC\",\"ABC fund\",\"Category\",\"Provider\",\"true\",\"US\",\"Index\",\"Blah blah\",\"2011-01-01\"", string.trim());
    }

    @Test
    public void testFundFromStringArr() {
        Fund expected = new Fund("ABC", "ABC fund", "Category", "Provider", true,
                "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        String[] arr = new String[]{"ABC", "ABC fund", "Category", "Provider", "true",
                "US", "Index", "Blah blah", "2011-01-01"};
        Fund fund = Fund.fromStrArr(arr);
        assertEquals(expected, fund);
    }

    @Test
    public void testTransactionToStringArr() {
        Fund fund = new Fund("ABC", "ABC fund", "Category", "Provider", true,
                "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        BuyTransaction buy = new BuyTransaction(fund, new LocalDate("2011-01-01"), new BigDecimal("100"), new BigDecimal("471.09"), BigDecimal.TEN);
        SellTransaction sell = new SellTransaction(fund, new LocalDate("2011-01-01"), new BigDecimal("100"), new BigDecimal("471.09"), BigDecimal.TEN);

        StringWriter stringWriter = new StringWriter();
        CSVWriter writer = new CSVWriter(stringWriter);

        writer.writeNext(buy.toStrArr());
        writer.writeNext(sell.toStrArr());

        String string = stringWriter.toString();
        assertEquals("\"BUY\",\"ABC\",\"2011-01-01\",\"100\",\"471.09\",\"10\"\n" +
                "\"SELL\",\"ABC\",\"2011-01-01\",\"100\",\"471.09\",\"10\"", string.trim());
    }

    @Test
    public void testTransactionFromStrArr() {
        Fund fund = new Fund("ABC", "ABC fund", "Category", "Provider", true,
                "US", "Index", "Blah blah", new LocalDate("2011-01-01"));

        BuyTransaction expectedBuy = new BuyTransaction(fund, new LocalDate("2011-01-01"), new BigDecimal("100"), new BigDecimal("471.09"), BigDecimal.TEN);
        SellTransaction expectedSell = new SellTransaction(fund, new LocalDate("2011-01-01"), new BigDecimal("100"), new BigDecimal("471.09"), BigDecimal.TEN);

        String[] buyArr = new String[]{"BUY", "ABC", "2011-01-01", "100", "471.09", "10"};
        String[] sellArr = new String[]{"SELL", "ABC", "2011-01-01", "100", "471.09", "10"};
        Transaction buy = AbstractTransaction.fromStrArr(fund, buyArr);
        Transaction sell = AbstractTransaction.fromStrArr(fund, sellArr);

        assertEquals(expectedBuy, buy);
        assertEquals(expectedSell, sell);


    }

}
