package com.mns.mojoinvest.server.engine.portfolio;

import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.Assert.*;

public class PositionTests {

    private final QuoteDao quoteDao = new QuoteDao(ObjectifyService.factory());
    private final Fund fund = new Fund("TEST", "Test fund", "Category", "Provider", true,
            "US", "Index", "Blah blah", new LocalDate("2011-01-01"));

    private final Fund wrongfund = new Fund("WRONG", "Test fund", "Category", "Provider", true,
            "US", "Index", "Blah blah", new LocalDate("2011-01-01"));

    public static final BigDecimal COMMISSION = new BigDecimal("15");

    private final BuyTransaction buy100 = new BuyTransaction(fund, new LocalDate("2011-02-01"), new BigDecimal("100"), new BigDecimal("471.09"), COMMISSION);
    private final BuyTransaction buy200 = new BuyTransaction(fund, new LocalDate("2011-03-01"), new BigDecimal("200"), new BigDecimal("471.09"), COMMISSION);
    private final SellTransaction sell50 = new SellTransaction(fund, new LocalDate("2011-03-01"), new BigDecimal("50"), new BigDecimal("573.20"), COMMISSION);
    private final SellTransaction sell50_2 = new SellTransaction(fund, new LocalDate("2011-04-01"), new BigDecimal("50"), new BigDecimal("498.30"), COMMISSION);
    private final SellTransaction sell100 = new SellTransaction(fund, new LocalDate("2011-03-01"), new BigDecimal("100"), new BigDecimal("2"), COMMISSION);
    private final SellTransaction sell200 = new SellTransaction(fund, new LocalDate("2011-03-01"), new BigDecimal("200"), new BigDecimal("2"), COMMISSION);

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
        assertTrue(position.open());
        position.add(sell50);
        position.add(sell50_2);
        assertFalse(position.open());
    }

    @Test
    public void testSplitSellAcrossLots() throws PortfolioException {
        Position position = new Position(quoteDao, fund);
        position.add(buy100);
        position.add(buy200);
        position.add(sell200);
        assertTrue(position.getLots().get(0).closed());
        assertEquals(new BigDecimal("100"), position.getLots().get(1).getRemainingQuantity());
        position.add(sell100);
        assertTrue(position.getLots().get(1).closed());
    }

    @Test(expected = PortfolioException.class)
    public void testSplitTooBigSellAcrossLotsFails() throws PortfolioException {
        Position position = new Position(quoteDao, fund);
        position.add(buy100);
        position.add(buy200);
        position.add(sell200);
        position.add(sell200);
    }

    @Test
    public void test() throws PortfolioException {
    }
//  @Test public  void test()  throws PortfolioException { }
//  @Test public  void test()  throws PortfolioException { }
//  @Test public  void test()  throws PortfolioException { }
//  @Test public  void test()  throws PortfolioException { }

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
