package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.Assert.*;

public class LotTests {

    public static final BigDecimal ONE_HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);
    public static final BigDecimal TWO_HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN).multiply(new BigDecimal("2"));
    public static final BigDecimal FIFTY = BigDecimal.TEN.multiply(new BigDecimal("5"));
    public static final BigDecimal TWO = BigDecimal.ONE.add(BigDecimal.ONE);
    public static final BigDecimal COMMISSION = new BigDecimal("15");

    Fund fund = new Fund("TEST", "Test fund", "Category", "Provider", true,
            "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
    BuyTransaction buy = new BuyTransaction(fund, new LocalDate("2011-02-01"), ONE_HUNDRED, new BigDecimal("471.09"), COMMISSION);
    SellTransaction sell1 = new SellTransaction(fund, new LocalDate("2011-03-01"), FIFTY, new BigDecimal("573.20"), COMMISSION);
    SellTransaction sell2 = new SellTransaction(fund, new LocalDate("2011-04-01"), FIFTY, new BigDecimal("498.30"), COMMISSION);
    SellTransaction sellTooLarge = new SellTransaction(fund, new LocalDate("2011-03-01"), TWO_HUNDRED, TWO, COMMISSION);

    @Test
    public void testCreateNewLot() {
        Lot lot = new Lot(buy);
        assertNotNull(lot.getOpeningTransaction());
        assertEquals(lot.getClosingTransactions().size(), 0);
    }

    @Test
    public void testInitialValues() {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("47124.00"), lot.getInitialInvestment());
        assertEquals(new BigDecimal("100"), lot.getInitialQuantity());
    }

    @Test
    public void testAddClosingTransaction() throws PortfolioException {
        Lot lot = new Lot(buy);
        lot.addClosingTransaction(sell1);
        assertEquals(lot.getClosingTransactions().size(), 1);
    }

    @Test(expected = PortfolioException.class)
    public void testAddTooLargeClosingTransactionFails() throws PortfolioException {
        Lot lot = new Lot(buy);
        lot.addClosingTransaction(sellTooLarge);
    }

    @Test
    public void testLotOpen() throws PortfolioException {
        Lot lot = new Lot(buy);
        lot.addClosingTransaction(sell1);
        assertFalse(lot.closed());
    }

    @Test
    public void testLotClosed() throws PortfolioException {
        Lot lot = new Lot(buy);
        lot.addClosingTransaction(sell1);
        lot.addClosingTransaction(sell2);
        assertTrue(lot.closed());
    }

    @Test
    public void testRemainingQuantity() throws PortfolioException {
        Lot lot = new Lot(buy);
        lot.addClosingTransaction(sell1);
        assertEquals(FIFTY, lot.getRemainingQuantity());
        lot.addClosingTransaction(sell2);
        assertEquals(BigDecimal.ZERO, lot.getRemainingQuantity());
    }

    @Test
    public void testCostBasis() throws PortfolioException {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("47124.00"), lot.costBasis());
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("23562.000"), lot.costBasis());
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("0.00"), lot.costBasis());
    }

    @Test
    public void testCashOut() throws PortfolioException {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("-47124.00"), lot.cashOut());
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("-47124.00"), lot.cashOut());
    }

    @Test
    public void testCashIn() throws PortfolioException {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("0"), lot.cashIn());
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("28645.00"), lot.cashIn());
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("53545.00"), lot.cashIn());
    }

    @Test
    public void testMarketValue() throws PortfolioException {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("50000"), lot.marketValue(new BigDecimal("500")));
        assertEquals(new BigDecimal("60000"), lot.marketValue(new BigDecimal("600")));
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("25000"), lot.marketValue(new BigDecimal("500")));
        assertEquals(new BigDecimal("30000"), lot.marketValue(new BigDecimal("600")));
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("0"), lot.marketValue(new BigDecimal("500")));
        assertEquals(new BigDecimal("0"), lot.marketValue(new BigDecimal("600")));
    }

    @Test
    public void testGain() throws PortfolioException {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("2876.00"), lot.gain(new BigDecimal("500")));
        assertEquals(new BigDecimal("12876.00"), lot.gain(new BigDecimal("600")));
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("1438.000"), lot.gain(new BigDecimal("500")));
        assertEquals(new BigDecimal("6438.000"), lot.gain(new BigDecimal("600")));
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("0.00"), lot.gain(new BigDecimal("500")));
        assertEquals(new BigDecimal("0.00"), lot.gain(new BigDecimal("600")));
    }

    @Test
    public void testTodaysGain() throws PortfolioException {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("150.0"), lot.todaysGain(new BigDecimal("1.5")));
        assertEquals(new BigDecimal("300"), lot.todaysGain(new BigDecimal("3")));
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("75.0"), lot.todaysGain(new BigDecimal("1.5")));
        assertEquals(new BigDecimal("150"), lot.todaysGain(new BigDecimal("3")));
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("0.0"), lot.todaysGain(new BigDecimal("1.5")));
        assertEquals(new BigDecimal("0"), lot.todaysGain(new BigDecimal("3")));
    }

    @Test
    public void testGainPercentage() throws PortfolioException {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("0.06103047"), lot.gainPercentage(new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2732366"), lot.gainPercentage(new BigDecimal("600")));
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("0.06103047"), lot.gainPercentage(new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2732366"), lot.gainPercentage(new BigDecimal("600")));
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("0"), lot.gainPercentage(new BigDecimal("500")));
        assertEquals(new BigDecimal("0"), lot.gainPercentage(new BigDecimal("600")));
    }

    @Test
    public void testReturnsGain() throws PortfolioException {

        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("2876.00"), lot.returnsGain(new BigDecimal("500")));
        assertEquals(new BigDecimal("12876.00"), lot.returnsGain(new BigDecimal("600")));
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("6521.00"), lot.returnsGain(new BigDecimal("500")));
        assertEquals(new BigDecimal("11521.00"), lot.returnsGain(new BigDecimal("600")));
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("6421.00"), lot.returnsGain(new BigDecimal("500")));
        assertEquals(new BigDecimal("6421.00"), lot.returnsGain(new BigDecimal("600")));
    }

    @Test
    public void testOverallReturn() throws PortfolioException {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("-0.1511756"), lot.overallReturn(new BigDecimal("400")));
        assertEquals(new BigDecimal("0.06103047"), lot.overallReturn(new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2732366"), lot.overallReturn(new BigDecimal("600")));
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("0.03227655"), lot.overallReturn(new BigDecimal("400")));
        assertEquals(new BigDecimal("0.1383796"), lot.overallReturn(new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2444826"), lot.overallReturn(new BigDecimal("600")));
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("0.1362575"), lot.overallReturn(new BigDecimal("400")));
        assertEquals(new BigDecimal("0.1362575"), lot.overallReturn(new BigDecimal("500")));
        assertEquals(new BigDecimal("0.1362575"), lot.overallReturn(new BigDecimal("600")));
    }


    //getRemainingQuantity(date)
//marketValue(date)
//returnsGain
//overallReturn
//gain
//todaysGain
//gainPercentage

}
