package com.mns.mojoinvest.server.engine.portfolio;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.*;

public class PositionTests {

    private final QuoteDao quoteDao = new QuoteDao(ObjectifyService.factory());
    private final Fund fund = new Fund("TEST", "Test fund", "Category", "Provider", true,
            "US", "Index", "Blah blah", new LocalDate("2011-01-01"));

    private final Fund wrongfund = new Fund("WRONG", "Test fund", "Category", "Provider", true,
            "US", "Index", "Blah blah", new LocalDate("2011-01-01"));

    public static final BigDecimal COMMISSION = new BigDecimal("15");

    private static final LocalDate buy100Date = new LocalDate("2011-02-01");
    private static final LocalDate buy200Date = new LocalDate("2011-03-01");
    private static final LocalDate sell50Date = new LocalDate("2011-03-15");
    private static final LocalDate sell50_2Date = new LocalDate("2011-04-01");
    private static final LocalDate sell100Date = new LocalDate("2011-05-01");
    private static final LocalDate sell200Date = new LocalDate("2011-06-01");


    private final BuyTransaction buy100 = new BuyTransaction(fund, buy100Date, new BigDecimal("100"), new BigDecimal("471.09"), COMMISSION);
    private final BuyTransaction buy200 = new BuyTransaction(fund, buy200Date, new BigDecimal("200"), new BigDecimal("471.09"), COMMISSION);
    private final SellTransaction sell50 = new SellTransaction(fund, sell50Date, new BigDecimal("50"), new BigDecimal("573.20"), COMMISSION);
    private final SellTransaction sell50_2 = new SellTransaction(fund, sell50_2Date, new BigDecimal("50"), new BigDecimal("498.30"), COMMISSION);
    private final SellTransaction sell100 = new SellTransaction(fund, sell100Date, new BigDecimal("100"), new BigDecimal("480.00"), COMMISSION);
    private final SellTransaction sell200 = new SellTransaction(fund, sell200Date, new BigDecimal("200"), new BigDecimal("520.00"), COMMISSION);

    private final LocalDatastoreServiceTestConfig config = new LocalDatastoreServiceTestConfig();
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(config);

    public static final List<Quote> quotes = Arrays.asList(
            new Quote("TEST", buy100Date, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("400"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false),
            new Quote("TEST", buy200Date, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("500"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false),
            new Quote("TEST", sell50Date, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("550"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false),
            new Quote("TEST", sell100Date, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("490"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false),
            new Quote("TEST", sell200Date, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("510"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false)
    );

    @Before
    public void setUp() {
        helper.setUp();
        quoteDao.put(quotes);
    }


    @Test
    public void testCreatePosition() throws PortfolioException {
        Position position = new Position(quoteDao, fund);
        assertNotNull(position);
    }

    @Test
    public void testAddBuyTransactionCreatesLot() throws PortfolioException {
        Position position = new Position(quoteDao, fund);
        position.add(buy100);
        assertEquals(1, position.getLots().size());
        position.add(buy200);
        assertEquals(2, position.getLots().size());
    }

    @Test
    public void testClosePosition() throws PortfolioException {
        Position position = new Position(quoteDao, fund);
        position.add(buy100);
        assertTrue(position.open(buy100Date));
        position.add(sell50);
        position.add(sell50_2);
        assertFalse(position.open(sell50_2Date));
    }

    @Test
    public void testSplitSellAcrossLots() throws PortfolioException {
        Position position = new Position(quoteDao, fund);
        position.add(buy100);
        position.add(buy200);
        position.add(sell200);
        assertTrue(position.getLots().get(0).closed(sell200Date));
        assertEquals(new BigDecimal("100"), position.getLots().get(1).getRemainingQuantity(sell200Date));
        //TODO: re add tests for closing lot(1) after sell200
//        position.add(sell100);
//        assertFalse(position.getLots().get(1).closed(sell200Date));
//        assertTrue(position.getLots().get(1).closed(sell100Date));
    }

    @Test(expected = PortfolioException.class)
    public void testSplitTooBigSellAcrossLotsFails() throws PortfolioException {
        Position position = new Position(quoteDao, fund);
        position.add(buy100);
        position.add(buy200);
        position.add(sell200);
        position.add(sell200);
    }


    //buy100 init investment = 47124
    //buy200 init investment = 94233

//    return getInitialInvestment().multiply(getRemainingQuantity(date)
//            .divide(getInitialQuantity(), MathContext.DECIMAL32));

    @Test
    public void testCostBasis() throws PortfolioException {
        Position position = new Position(quoteDao, fund);
        position.add(buy100);
        assertEquals(new BigDecimal("47124.00"), position.costBasis(buy100Date));
        position.add(buy200);
        assertEquals(new BigDecimal("47124.00"), position.costBasis(buy100Date));
        assertEquals(new BigDecimal("141357.00"), position.costBasis(buy200Date));
        position.add(sell50);
        assertEquals(new BigDecimal("117795.000"), position.costBasis(sell50Date));
        position.add(sell100);
        assertEquals(new BigDecimal("117795.000"), position.costBasis(sell50Date));
        assertEquals(new BigDecimal("70674.7500"), position.costBasis(sell100Date));
    }

    @Test
    public void testCashOut() throws PortfolioException {
        Position position = new Position(quoteDao, fund);
        position.add(buy100);
        assertEquals(new BigDecimal("-47124.00"), position.cashOut(buy100Date));
        position.add(buy200);
        assertEquals(new BigDecimal("-47124.00"), position.cashOut(buy100Date));
        assertEquals(new BigDecimal("-141357.00"), position.cashOut(buy200Date));
        position.add(sell50);
        assertEquals(new BigDecimal("-47124.00"), position.cashOut(buy100Date));
        assertEquals(new BigDecimal("-141357.00"), position.cashOut(sell50Date));

    }

    @Test
    public void testCashIn() throws PortfolioException {
        Position position = new Position(quoteDao, fund);
        position.add(buy100);
        assertEquals(new BigDecimal("0"), position.cashIn(buy100Date));
        position.add(buy200);
        assertEquals(new BigDecimal("0"), position.cashIn(buy100Date));
        assertEquals(new BigDecimal("0"), position.cashIn(buy200Date));
        position.add(sell50);
        assertEquals(new BigDecimal("0"), position.cashIn(buy100Date));
        assertEquals(new BigDecimal("28645.00"), position.cashIn(sell50Date));
    }


//    buy100Date "400")
//    buy200Date "500")
//    sell50Date "550")
//    sell100Date "490")
//    sell200Date "510")


    @Test
    public void testMarketValue() throws PortfolioException {
        Position position = new Position(quoteDao, fund);
        position.add(buy100);
        assertEquals(new BigDecimal("40000"), position.marketValue(buy100Date));
        position.add(buy200);
        assertEquals(new BigDecimal("40000"), position.marketValue(buy100Date));
        assertEquals(new BigDecimal("150000"), position.marketValue(buy200Date));
        position.add(sell50);
        assertEquals(new BigDecimal("137500"), position.marketValue(sell50Date));
        position.add(sell100);
        assertEquals(new BigDecimal("137500"), position.marketValue(sell50Date));
        assertEquals(new BigDecimal("76500"), position.marketValue(sell200Date));
    }

    @Test
    public void testGain() throws PortfolioException {
        Position position = new Position(quoteDao, fund);
        position.add(buy100);
        position.add(buy200);
        position.gain(buy200Date);
    }

    @Test
    public void testGainPercentage() throws PortfolioException {
        Position position = new Position(quoteDao, fund);
        position.add(buy100);
        position.add(buy200);
        position.gainPercentage(buy200Date);
    }

    @Test
    public void testTotalReturn() throws PortfolioException {
        Position position = new Position(quoteDao, fund);
        position.add(buy100);
        position.add(buy200);
        position.totalReturn(buy200Date);
    }

//Position
//costBasis
//marketValue
//gain
//todaysGain
//gainPercentage
//totalReturn
//returnsGain
//cashOut


}
