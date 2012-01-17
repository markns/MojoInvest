package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.Assert.*;

public class LotTests {

    public static final BigDecimal COMMISSION = new BigDecimal("15");

    private final Fund fund = new Fund("TEST", "Test fund", "Category", "Provider", true,
            "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
    private final LocalDate buyDate = new LocalDate("2011-02-01");
    private final LocalDate sell1Date = new LocalDate("2011-03-01");
    private final LocalDate sell2Date = new LocalDate("2011-04-01");
    private final LocalDate sellTooLargeDate = new LocalDate("2011-03-01");
    private final BuyTransaction buy = new BuyTransaction(fund, buyDate, new BigDecimal("100"), new BigDecimal("471.09"), COMMISSION);
    private final SellTransaction sell1 = new SellTransaction(fund, sell1Date, new BigDecimal("50"), new BigDecimal("573.20"), COMMISSION);
    private final SellTransaction sell2 = new SellTransaction(fund, sell2Date, new BigDecimal("50"), new BigDecimal("498.30"), COMMISSION);
    private final SellTransaction sellTooLarge = new SellTransaction(fund, sellTooLargeDate, new BigDecimal("200"), new BigDecimal("2"), COMMISSION);

    @Test
    public void testCreateNewLot() {
        Lot lot = new Lot(buy);
        assertNotNull(lot.getOpeningTransaction());
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
        assertEquals(lot.getClosingTransactions(sell1Date.minusDays(1)).size(), 0);
        assertEquals(lot.getClosingTransactions(sell1Date).size(), 1);
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
        assertFalse(lot.closed(sell1Date));
    }

    @Test
    public void testLotClosed() throws PortfolioException {
        Lot lot = new Lot(buy);
        lot.addClosingTransaction(sell1);
        lot.addClosingTransaction(sell2);
        assertTrue(lot.closed(sell2Date));
    }

    @Test
    public void testRemainingQuantity() throws PortfolioException {
        Lot lot = new Lot(buy);
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("50"), lot.getRemainingQuantity(sell1Date));
        lot.addClosingTransaction(sell2);
        assertEquals(BigDecimal.ZERO, lot.getRemainingQuantity(sell2Date));
    }

    @Test
    public void testCostBasis() throws PortfolioException {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("47124.00"), lot.costBasis(buyDate));
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("23562.000"), lot.costBasis(sell1Date));
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("23562.000"), lot.costBasis(sell1Date));
        assertEquals(new BigDecimal("0.00"), lot.costBasis(sell2Date));
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
        assertEquals(new BigDecimal("0"), lot.cashIn(buyDate));
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("28645.00"), lot.cashIn(sell1Date));
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("28645.00"), lot.cashIn(sell1Date));
        assertEquals(new BigDecimal("53545.00"), lot.cashIn(sell2Date));
    }

    @Test
    public void testMarketValue() throws PortfolioException {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("50000"), lot.marketValue(buyDate, new BigDecimal("500")));
        assertEquals(new BigDecimal("60000"), lot.marketValue(buyDate, new BigDecimal("600")));
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("25000"), lot.marketValue(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("30000"), lot.marketValue(sell1Date, new BigDecimal("600")));
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("25000"), lot.marketValue(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("30000"), lot.marketValue(sell1Date, new BigDecimal("600")));
        assertEquals(new BigDecimal("0"), lot.marketValue(sell2Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0"), lot.marketValue(sell2Date, new BigDecimal("600")));
    }

    @Test
    public void testGain() throws PortfolioException {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("2876.00"), lot.gain(buyDate, new BigDecimal("500")));
        assertEquals(new BigDecimal("12876.00"), lot.gain(buyDate, new BigDecimal("600")));
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("1438.000"), lot.gain(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("6438.000"), lot.gain(sell1Date, new BigDecimal("600")));
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("1438.000"), lot.gain(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("6438.000"), lot.gain(sell1Date, new BigDecimal("600")));
        assertEquals(new BigDecimal("0.00"), lot.gain(sell2Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.00"), lot.gain(sell2Date, new BigDecimal("600")));
    }

    @Test
    public void testTodaysGain() throws PortfolioException {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("150.0"), lot.todaysGain(buyDate, new BigDecimal("1.5")));
        assertEquals(new BigDecimal("300"), lot.todaysGain(buyDate, new BigDecimal("3")));
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("75.0"), lot.todaysGain(sell1Date, new BigDecimal("1.5")));
        assertEquals(new BigDecimal("150"), lot.todaysGain(sell1Date, new BigDecimal("3")));
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("75.0"), lot.todaysGain(sell1Date, new BigDecimal("1.5")));
        assertEquals(new BigDecimal("150"), lot.todaysGain(sell1Date, new BigDecimal("3")));

        assertEquals(new BigDecimal("0.0"), lot.todaysGain(sell2Date, new BigDecimal("1.5")));
        assertEquals(new BigDecimal("0"), lot.todaysGain(sell2Date, new BigDecimal("3")));
    }

    @Test
    public void testGainPercentage() throws PortfolioException {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("0.06103047"), lot.gainPercentage(buyDate, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2732366"), lot.gainPercentage(buyDate, new BigDecimal("600")));
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("0.06103047"), lot.gainPercentage(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2732366"), lot.gainPercentage(sell1Date, new BigDecimal("600")));
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("0.06103047"), lot.gainPercentage(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2732366"), lot.gainPercentage(sell1Date, new BigDecimal("600")));

        assertEquals(new BigDecimal("0"), lot.gainPercentage(sell2Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0"), lot.gainPercentage(sell2Date, new BigDecimal("600")));
    }

    @Test
    public void testReturnsGain() throws PortfolioException {

        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("2876.00"), lot.returnsGain(buyDate, new BigDecimal("500")));
        assertEquals(new BigDecimal("12876.00"), lot.returnsGain(buyDate, new BigDecimal("600")));
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("6521.00"), lot.returnsGain(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("11521.00"), lot.returnsGain(sell1Date, new BigDecimal("600")));
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("6521.00"), lot.returnsGain(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("11521.00"), lot.returnsGain(sell1Date, new BigDecimal("600")));

        assertEquals(new BigDecimal("6421.00"), lot.returnsGain(sell2Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("6421.00"), lot.returnsGain(sell2Date, new BigDecimal("600")));
    }

    @Test
    public void testOverallReturn() throws PortfolioException {
        Lot lot = new Lot(buy);
        assertEquals(new BigDecimal("-0.1511756"), lot.overallReturn(buyDate, new BigDecimal("400")));
        assertEquals(new BigDecimal("0.06103047"), lot.overallReturn(buyDate, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2732366"), lot.overallReturn(buyDate, new BigDecimal("600")));
        lot.addClosingTransaction(sell1);
        assertEquals(new BigDecimal("0.03227655"), lot.overallReturn(sell1Date, new BigDecimal("400")));
        assertEquals(new BigDecimal("0.1383796"), lot.overallReturn(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2444826"), lot.overallReturn(sell1Date, new BigDecimal("600")));
        lot.addClosingTransaction(sell2);
        assertEquals(new BigDecimal("0.03227655"), lot.overallReturn(sell1Date, new BigDecimal("400")));
        assertEquals(new BigDecimal("0.1383796"), lot.overallReturn(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2444826"), lot.overallReturn(sell1Date, new BigDecimal("600")));

        assertEquals(new BigDecimal("0.1362575"), lot.overallReturn(sell2Date, new BigDecimal("400")));
        assertEquals(new BigDecimal("0.1362575"), lot.overallReturn(sell2Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.1362575"), lot.overallReturn(sell2Date, new BigDecimal("600")));
    }


    //getRemainingQuantity(date)
//marketValue(date)
//returnsGain
//overallReturn
//gain
//todaysGain
//gainPercentage

}