package com.mns.alphaposition.server.engine;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.mns.alphaposition.server.data.FundSet;
import com.mns.alphaposition.server.data.QuoteSet;
import com.mns.alphaposition.server.engine.execution.Executor;
import com.mns.alphaposition.server.engine.execution.NextTradingDayExecutor;
import com.mns.alphaposition.server.engine.model.*;
import com.mns.alphaposition.server.engine.portfolio.Portfolio;
import com.mns.alphaposition.server.engine.portfolio.PortfolioProvider;
import com.mns.alphaposition.server.engine.portfolio.SimplePortfolio;
import com.mns.alphaposition.server.engine.strategy.MomentumStrategy;
import com.mns.alphaposition.server.engine.strategy.SimpleRankingStrategy;
import com.mns.alphaposition.server.engine.strategy.StrategyException;
import com.mns.alphaposition.server.util.QuoteUtils;
import com.mns.alphaposition.shared.params.MomentumStrategyParams;
import com.mns.alphaposition.shared.params.PortfolioParams;
import com.mns.alphaposition.shared.params.RankingStrategyParams;
import com.mns.alphaposition.shared.params.SimpleRankingStrategyParams;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class MomentumStrategyTests {

    private final QuoteDao quoteDao = new QuoteDao(ObjectifyService.factory());
    private final RankingDao rankingDao = new RankingDao(ObjectifyService.factory());
    private final FundDao fundDao = new FundDao(ObjectifyService.factory());

    private final PortfolioParams portfolioParams =
            new PortfolioParams(new BigDecimal("10000"), new BigDecimal("12.95"));

    private final PortfolioProvider portfolioProvider = new PortfolioProvider();
    private final Portfolio portfolio = new SimplePortfolio(quoteDao, portfolioParams);

    private final Executor executor = new NextTradingDayExecutor(portfolioProvider, quoteDao);

    private final RankingStrategyParams rankingStrategyParams = new SimpleRankingStrategyParams(10, 9);
    private final MomentumStrategyParams strategyParams = new MomentumStrategyParams(1, rankingStrategyParams, 3);

    private final LocalDate fromDate = new LocalDate(2005, 1, 1);
    private final LocalDate toDate = new LocalDate(2011, 1, 1);

//    private final String[] fundStrs = {"iShares FTSE China 25 Index Fund,FXI,China Region,iShares,7030000000,0.0072,2004-10-05,15407400",
//            "iShares MSCI Taiwan Index,EWT,China Region,iShares,3450000000,0.0071,2000-06-20,11161600",
//            "iShares Silver Trust,SLV,Commodities Precious Metals,iShares,12350000000,0.005,2006-04-21,71429800",
//            "iShares S&P Global Telecommunications,IXP,Communications,iShares,413300000,0.0048,2001-11-12,39416",
//            "iShares Dow Jones US Consumer Services,IYC,Consumer Discretionary,iShares,247920000,0.0047,2000-06-12,38952"};

    public static final String provider = "State Street Global Advisors";
    private final List<Fund> funds = FundSet.getFundsByProvider(Arrays.asList(provider));
    private final List<Quote> quotes = QuoteSet.getQuotesByProvider(Arrays.asList(provider));

    private final LocalDatastoreServiceTestConfig config = new LocalDatastoreServiceTestConfig();
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(config);

    long time = 0L;

    @Before
    public void setUp() {
        helper.setUp();
        quoteDao.put(quotes);
//        portfolioProvider.setPortfolio(portfolio);
        time = System.currentTimeMillis();
        for (Fund fund : funds) {
            List<Quote> quotes = quoteDao.query(fund);
//            QuoteUtils.sortByDate(quotes);
//            System.out.println(quotes.get(0).getDate());
//            List<Quote> missingQuotes = QuoteUtils.getMissingQuotes(quotes.get(0).getDate(),
            List<Quote> missingQuotes = QuoteUtils.getMissingQuotes(fund.getInceptionDate(),
                    new LocalDate("2011-06-24"), quotes);

            quoteDao.put(missingQuotes);
        }
        System.out.println(System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
    }

    @Test
    public void testMomentumStrategy() throws StrategyException {
        System.out.println("Starting run of testMomentumStrategy");
        SimpleRankingStrategy rankingStrategy = new SimpleRankingStrategy(quoteDao);
        MomentumStrategy tradingStrategy = new MomentumStrategy(rankingStrategy, executor, portfolioProvider,
                rankingDao, fundDao);
        time = System.currentTimeMillis();
        tradingStrategy.execute(fromDate, toDate, funds, strategyParams);
        System.out.println(System.currentTimeMillis() - time);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }


}
