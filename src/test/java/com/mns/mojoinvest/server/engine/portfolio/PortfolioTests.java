package com.mns.mojoinvest.server.engine.portfolio;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.shared.params.PortfolioParams;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.*;

public class PortfolioTests {

    private final PortfolioParams params = new PortfolioParams(50000.00d, 15.00d);
    private final PortfolioParams loadsofcash = new PortfolioParams(10000000d, 15.0d);

    private final QuoteDao quoteDao = new QuoteDao(ObjectifyService.factory());
    private final Fund ABC = new Fund("ABC", "ABC fund", "Category", "Provider", true,
            "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
    private final Fund DEF = new Fund("DEF", "DEF fund", "Category", "Provider", true,
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

    private final BuyTransaction buyABC100 = new BuyTransaction(ABC, buy100Date, new BigDecimal("100"), new BigDecimal("471.09"), COMMISSION);
    private final BuyTransaction buyABC200 = new BuyTransaction(ABC, buy200Date, new BigDecimal("200"), new BigDecimal("471.09"), COMMISSION);
    private final SellTransaction sellABC50 = new SellTransaction(ABC, sell50Date, new BigDecimal("50"), new BigDecimal("573.20"), COMMISSION);
    private final SellTransaction sellABC50_2 = new SellTransaction(ABC, sell50_2Date, new BigDecimal("50"), new BigDecimal("498.30"), COMMISSION);
    private final SellTransaction sellABC100 = new SellTransaction(ABC, sell100Date, new BigDecimal("100"), new BigDecimal("480.00"), COMMISSION);
    private final SellTransaction sellABC200 = new SellTransaction(ABC, sell200Date, new BigDecimal("200"), new BigDecimal("520.00"), COMMISSION);

    private final BuyTransaction buyDEF100 = new BuyTransaction(DEF, buy100Date, new BigDecimal("100"), new BigDecimal("471.09"), COMMISSION);
    private final BuyTransaction buyDEF200 = new BuyTransaction(DEF, buy200Date, new BigDecimal("200"), new BigDecimal("471.09"), COMMISSION);
    private final SellTransaction sellDEF50 = new SellTransaction(DEF, sell50Date, new BigDecimal("50"), new BigDecimal("573.20"), COMMISSION);
    private final SellTransaction sellDEF50_2 = new SellTransaction(DEF, sell50_2Date, new BigDecimal("50"), new BigDecimal("498.30"), COMMISSION);
    private final SellTransaction sellDEF100 = new SellTransaction(DEF, sell100Date, new BigDecimal("100"), new BigDecimal("480.00"), COMMISSION);
    private final SellTransaction sellDEF200 = new SellTransaction(DEF, sell200Date, new BigDecimal("200"), new BigDecimal("520.00"), COMMISSION);

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
    public void testCreatePortfolio() {
        Portfolio portfolio = new SimplePortfolio(quoteDao, params);
        assertEquals(new BigDecimal("50000.0"), portfolio.getCash());
        assertEquals(new BigDecimal("15.0"), portfolio.getTransactionCost());
        assertEquals(0, portfolio.getPositions().size());
    }

    @Test
    public void testPortfolioContains() throws PortfolioException {
        Portfolio portfolio = new SimplePortfolio(quoteDao, params);
        portfolio.add(buyABC100);
        assertTrue(portfolio.contains(ABC, buy100Date));
        assertFalse(portfolio.contains(ABC, buy100Date.minusDays(1)));
        assertFalse(portfolio.contains(DEF, buy100Date));
    }

    @Test
    public void testAddAndGetPosition() throws PortfolioException {
        Portfolio portfolio = new SimplePortfolio(quoteDao, params);
        portfolio.add(buyABC100);
        Position position = portfolio.getPosition(ABC);
        assertEquals(ABC, position.getFund());
    }

    @Test(expected = PortfolioException.class)
    public void testAddTooLargeFails() throws PortfolioException {
        Portfolio portfolio = new SimplePortfolio(quoteDao, params);
        portfolio.add(buyABC100);
        portfolio.add(buyABC100); //Not enough cash to add the same transaction again
    }

    @Test
    public void testGetPositions() throws PortfolioException {
        Portfolio portfolio = new SimplePortfolio(quoteDao, loadsofcash);
        portfolio.add(buyABC100);
        assertEquals(1, portfolio.getPositions().size());
        portfolio.add(buyDEF200);
        assertEquals(2, portfolio.getPositions().size());
        assertEquals(2, portfolio.getPositions().size());
        portfolio.add(sellABC100);
        assertEquals(2, portfolio.getPositions().size());
        assertEquals(2, portfolio.getPositions().size());

    }

    @Test
    public void testOpenPositionCount() throws PortfolioException {
        Portfolio portfolio = new SimplePortfolio(quoteDao, loadsofcash);
        portfolio.add(buyABC100);
        assertEquals(1, portfolio.openPositionCount(buy100Date));
        portfolio.add(buyDEF200);
        assertEquals(1, portfolio.openPositionCount(buy100Date));
        assertEquals(2, portfolio.openPositionCount(buy200Date));
        portfolio.add(sellABC100);
        assertEquals(1, portfolio.openPositionCount(sell100Date));
        assertEquals(2, portfolio.openPositionCount(sell100Date.minusDays(1)));
    }

    @Test
    public void testGetActiveFunds() throws PortfolioException {
        Portfolio portfolio = new SimplePortfolio(quoteDao, loadsofcash);
        portfolio.add(buyABC100);
        assertEquals(1, portfolio.getActiveFunds(buy100Date).size());
        portfolio.add(buyDEF200);
        assertEquals(1, portfolio.getActiveFunds(buy100Date).size());
        assertEquals(2, portfolio.getActiveFunds(buy200Date).size());
        portfolio.add(sellABC100);
        assertEquals(1, portfolio.getActiveFunds(sell100Date).size());
        assertEquals(2, portfolio.getActiveFunds(sell100Date.minusDays(1)).size());

    }

//SimplePortfolio
//costBasis
//marketValue
//gain
//gainPercentage
//overallReturn
//returnsGain
//cashOut

}
