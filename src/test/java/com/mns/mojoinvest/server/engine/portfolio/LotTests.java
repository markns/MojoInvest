package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.DataAccessException;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.mns.mojoinvest.server.engine.strategy.MomentumStrategyTests.anyLocalDate;
import static com.mns.mojoinvest.server.mock.Matchers.anyFund;
import static junit.framework.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LotTests {

    public static final BigDecimal COMMISSION = new BigDecimal("15");

    private final Fund fund = new Fund("TEST", "Test fund", "Category", "Provider", true,
            "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
    private final LocalDate buyDate = new LocalDate("2011-02-01");
    private final LocalDate sell1Date = new LocalDate("2011-03-01");
    private final LocalDate sell2Date = new LocalDate("2011-04-01");
    private final LocalDate sellTooLargeDate = new LocalDate("2011-03-01");
    private final BuyTransaction buy = new BuyTransaction("TEST", buyDate, new BigDecimal("100"), new BigDecimal("471.09"), COMMISSION);
    private final SellTransaction sell1 = new SellTransaction("TEST", sell1Date, new BigDecimal("50"), new BigDecimal("573.20"), COMMISSION);
    private final SellTransaction sell2 = new SellTransaction("TEST", sell2Date, new BigDecimal("50"), new BigDecimal("498.30"), COMMISSION);
    private final SellTransaction sellTooLarge = new SellTransaction("TEST", sellTooLargeDate, new BigDecimal("200"), new BigDecimal("2"), COMMISSION);

    @Mock
    private QuoteDao quoteDao;

    @Test
    public void testCreateNewLot() {
        Lot lot = new Lot(quoteDao, buy);
        assertNotNull(lot.getOpeningTransaction());
    }

    @Test
    public void testInitialValues() {
        Lot lot = new Lot(quoteDao, buy);
        assertEquals(new BigDecimal("47124.00"), lot.getInitialInvestment());
        assertEquals(new BigDecimal("100"), lot.getInitialQuantity());
    }

    @Test
    public void testAddClosingTransaction() throws PortfolioException {
        Lot lot = new Lot(quoteDao, buy);
        lot.addSellTransaction(sell1);
        assertEquals(lot.getSellTransactions(sell1Date.minusDays(1)).size(), 0);
        assertEquals(lot.getSellTransactions(sell1Date).size(), 1);
    }

    @Test(expected = PortfolioException.class)
    public void testAddTooLargeClosingTransactionFails() throws PortfolioException {
        Lot lot = new Lot(quoteDao, buy);
        lot.addSellTransaction(sellTooLarge);
    }

    @Test
    public void testLotOpen() throws PortfolioException {
        Lot lot = new Lot(quoteDao, buy);
        lot.addSellTransaction(sell1);
        assertFalse(lot.closed(sell1Date));
    }

    @Test
    public void testLotClosed() throws PortfolioException {
        Lot lot = new Lot(quoteDao, buy);
        lot.addSellTransaction(sell1);
        lot.addSellTransaction(sell2);
        assertTrue(lot.closed(sell2Date));
    }

    @Test
    public void testRemainingQuantity() throws PortfolioException {
        Lot lot = new Lot(quoteDao, buy);
        lot.addSellTransaction(sell1);
        assertEquals(new BigDecimal("50"), lot.getRemainingQuantity(sell1Date));
        lot.addSellTransaction(sell2);
        assertEquals(BigDecimal.ZERO, lot.getRemainingQuantity(sell2Date));
    }

    @Test
    public void testCostBasis() throws PortfolioException {
        Lot lot = new Lot(quoteDao, buy);
        assertEquals(new BigDecimal("47124.00"), lot.costBasis(buyDate));
        lot.addSellTransaction(sell1);
        assertEquals(new BigDecimal("23562.000"), lot.costBasis(sell1Date));
        lot.addSellTransaction(sell2);
        assertEquals(new BigDecimal("23562.000"), lot.costBasis(sell1Date));
        assertEquals(new BigDecimal("0.00"), lot.costBasis(sell2Date));
    }

    @Test
    public void testCashOut() throws PortfolioException {
        Lot lot = new Lot(quoteDao, buy);
        assertEquals(new BigDecimal("-47124.00"), lot.cashOut());
        lot.addSellTransaction(sell1);
        assertEquals(new BigDecimal("-47124.00"), lot.cashOut());
    }

    @Test
    public void testCashIn() throws PortfolioException {
        Lot lot = new Lot(quoteDao, buy);
        assertEquals(new BigDecimal("0"), lot.cashIn(buyDate));
        lot.addSellTransaction(sell1);
        assertEquals(new BigDecimal("28645.00"), lot.cashIn(sell1Date));
        lot.addSellTransaction(sell2);
        assertEquals(new BigDecimal("28645.00"), lot.cashIn(sell1Date));
        assertEquals(new BigDecimal("53545.00"), lot.cashIn(sell2Date));
    }

    @Test
    public void testMarketValue() throws PortfolioException {
        Lot lot = new Lot(quoteDao, buy);
        assertEquals(new BigDecimal("50000"), lot.marketValue(buyDate, new BigDecimal("500")));
        assertEquals(new BigDecimal("60000"), lot.marketValue(buyDate, new BigDecimal("600")));
        lot.addSellTransaction(sell1);
        assertEquals(new BigDecimal("25000"), lot.marketValue(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("30000"), lot.marketValue(sell1Date, new BigDecimal("600")));
        lot.addSellTransaction(sell2);
        assertEquals(new BigDecimal("25000"), lot.marketValue(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("30000"), lot.marketValue(sell1Date, new BigDecimal("600")));
        assertEquals(new BigDecimal("0"), lot.marketValue(sell2Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0"), lot.marketValue(sell2Date, new BigDecimal("600")));
    }

    @Test
    public void testGain() throws PortfolioException {
        Lot lot = new Lot(quoteDao, buy);
        assertEquals(new BigDecimal("2876.00"), lot.gain(buyDate, new BigDecimal("500")));
        assertEquals(new BigDecimal("12876.00"), lot.gain(buyDate, new BigDecimal("600")));
        lot.addSellTransaction(sell1);
        assertEquals(new BigDecimal("1438.000"), lot.gain(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("6438.000"), lot.gain(sell1Date, new BigDecimal("600")));
        lot.addSellTransaction(sell2);
        assertEquals(new BigDecimal("1438.000"), lot.gain(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("6438.000"), lot.gain(sell1Date, new BigDecimal("600")));
        assertEquals(new BigDecimal("0.00"), lot.gain(sell2Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.00"), lot.gain(sell2Date, new BigDecimal("600")));
    }

    @Test
    public void testTodaysGain() throws PortfolioException {
        Lot lot = new Lot(quoteDao, buy);
        assertEquals(new BigDecimal("150.0"), lot.todaysGain(buyDate, new BigDecimal("1.5")));
        assertEquals(new BigDecimal("300"), lot.todaysGain(buyDate, new BigDecimal("3")));
        lot.addSellTransaction(sell1);
        assertEquals(new BigDecimal("75.0"), lot.todaysGain(sell1Date, new BigDecimal("1.5")));
        assertEquals(new BigDecimal("150"), lot.todaysGain(sell1Date, new BigDecimal("3")));
        lot.addSellTransaction(sell2);
        assertEquals(new BigDecimal("75.0"), lot.todaysGain(sell1Date, new BigDecimal("1.5")));
        assertEquals(new BigDecimal("150"), lot.todaysGain(sell1Date, new BigDecimal("3")));

        assertEquals(new BigDecimal("0.0"), lot.todaysGain(sell2Date, new BigDecimal("1.5")));
        assertEquals(new BigDecimal("0"), lot.todaysGain(sell2Date, new BigDecimal("3")));
    }

    @Test
    public void testGainPercentage() throws PortfolioException {
        Lot lot = new Lot(quoteDao, buy);
        assertEquals(new BigDecimal("0.06103047"), lot.gainPercentage(buyDate, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2732366"), lot.gainPercentage(buyDate, new BigDecimal("600")));
        lot.addSellTransaction(sell1);
        assertEquals(new BigDecimal("0.06103047"), lot.gainPercentage(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2732366"), lot.gainPercentage(sell1Date, new BigDecimal("600")));
        lot.addSellTransaction(sell2);
        assertEquals(new BigDecimal("0.06103047"), lot.gainPercentage(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2732366"), lot.gainPercentage(sell1Date, new BigDecimal("600")));

        assertEquals(new BigDecimal("0"), lot.gainPercentage(sell2Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0"), lot.gainPercentage(sell2Date, new BigDecimal("600")));
    }

    @Test
    public void testReturnsGain() throws PortfolioException {

        Lot lot = new Lot(quoteDao, buy);
        assertEquals(new BigDecimal("2876.00"), lot.returnsGain(buyDate, new BigDecimal("500")));
        assertEquals(new BigDecimal("12876.00"), lot.returnsGain(buyDate, new BigDecimal("600")));
        lot.addSellTransaction(sell1);
        assertEquals(new BigDecimal("6521.00"), lot.returnsGain(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("11521.00"), lot.returnsGain(sell1Date, new BigDecimal("600")));
        lot.addSellTransaction(sell2);
        assertEquals(new BigDecimal("6521.00"), lot.returnsGain(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("11521.00"), lot.returnsGain(sell1Date, new BigDecimal("600")));

        assertEquals(new BigDecimal("6421.00"), lot.returnsGain(sell2Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("6421.00"), lot.returnsGain(sell2Date, new BigDecimal("600")));
    }

    @Test
    public void testOverallReturn() throws PortfolioException {
        Lot lot = new Lot(quoteDao, buy);
        assertEquals(new BigDecimal("-0.1511756"), lot.overallReturn(buyDate, new BigDecimal("400")));
        assertEquals(new BigDecimal("0.06103047"), lot.overallReturn(buyDate, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2732366"), lot.overallReturn(buyDate, new BigDecimal("600")));
        lot.addSellTransaction(sell1);
        assertEquals(new BigDecimal("0.03227655"), lot.overallReturn(sell1Date, new BigDecimal("400")));
        assertEquals(new BigDecimal("0.1383796"), lot.overallReturn(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2444826"), lot.overallReturn(sell1Date, new BigDecimal("600")));
        lot.addSellTransaction(sell2);
        assertEquals(new BigDecimal("0.03227655"), lot.overallReturn(sell1Date, new BigDecimal("400")));
        assertEquals(new BigDecimal("0.1383796"), lot.overallReturn(sell1Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.2444826"), lot.overallReturn(sell1Date, new BigDecimal("600")));

        assertEquals(new BigDecimal("0.1362575"), lot.overallReturn(sell2Date, new BigDecimal("400")));
        assertEquals(new BigDecimal("0.1362575"), lot.overallReturn(sell2Date, new BigDecimal("500")));
        assertEquals(new BigDecimal("0.1362575"), lot.overallReturn(sell2Date, new BigDecimal("600")));
    }


    private final Quote dummyQuote = new Quote("DUMMY", new LocalDate("2000-01-15"),
            BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, new BigDecimal("2"),
            BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, false);

    public static final BigDecimal ZERO = BigDecimal.ZERO;
    public static final BigDecimal HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);
    public static final BigDecimal TWO_HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN).multiply(new BigDecimal("2"));
    public static final BigDecimal FIFTY = BigDecimal.TEN.multiply(new BigDecimal("5"));

    @Test
    public void testMarketValueSeries() throws PortfolioException, DataAccessException {
        when(quoteDao.get(anyFund(), anyLocalDate())).thenReturn(dummyQuote);
        Lot lot = new Lot(quoteDao, buy);
        List<LocalDate> dates = Arrays.asList(buyDate.minusDays(1), buyDate, buyDate.plusDays(1),
                sell1Date.minusDays(1), sell1Date, sell1Date.plusDays(1),
                sell2Date.minusDays(1), sell2Date, sell2Date.plusDays(1));

        List<BigDecimal> values = lot.marketValue(dates);
        List<BigDecimal> expected = Arrays.asList(ZERO, TWO_HUNDRED, TWO_HUNDRED, TWO_HUNDRED, TWO_HUNDRED, TWO_HUNDRED, TWO_HUNDRED, TWO_HUNDRED, TWO_HUNDRED);
        assertEquals(expected, values);
        lot.addSellTransaction(sell1);
        values = lot.marketValue(dates);
        expected = Arrays.asList(ZERO, TWO_HUNDRED, TWO_HUNDRED, TWO_HUNDRED, HUNDRED, HUNDRED, HUNDRED, HUNDRED, HUNDRED);
        assertEquals(expected, values);
        lot.addSellTransaction(sell2);
        values = lot.marketValue(dates);
        expected = Arrays.asList(ZERO, TWO_HUNDRED, TWO_HUNDRED, TWO_HUNDRED, HUNDRED, HUNDRED, HUNDRED, ZERO, ZERO);
        assertEquals(expected, values);

    }

    @Test
    public void testRemainingQuantitySeries() throws PortfolioException {
        Lot lot = new Lot(quoteDao, buy);
        List<LocalDate> dates = Arrays.asList(buyDate.minusDays(1), buyDate, buyDate.plusDays(1),
                sell1Date.minusDays(1), sell1Date, sell1Date.plusDays(1),
                sell2Date.minusDays(1), sell2Date, sell2Date.plusDays(1));
        List<BigDecimal> values = lot.getRemainingQuantity(dates);
        List<BigDecimal> expected = Arrays.asList(ZERO, HUNDRED, HUNDRED, HUNDRED, HUNDRED, HUNDRED, HUNDRED, HUNDRED, HUNDRED);
        assertEquals(expected, values);
        lot.addSellTransaction(sell1);
        values = lot.getRemainingQuantity(dates);
        expected = Arrays.asList(ZERO, HUNDRED, HUNDRED, HUNDRED, FIFTY, FIFTY, FIFTY, FIFTY, FIFTY);
        assertEquals(expected, values);
        lot.addSellTransaction(sell2);
        values = lot.getRemainingQuantity(dates);
        expected = Arrays.asList(ZERO, HUNDRED, HUNDRED, HUNDRED, FIFTY, FIFTY, FIFTY, ZERO, ZERO);
        assertEquals(expected, values);
    }

}
