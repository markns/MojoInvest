package com.mns.alphaposition.server.engine;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.mns.alphaposition.server.data.QuoteSet;
import com.mns.alphaposition.server.engine.execution.Executor;
import com.mns.alphaposition.server.engine.model.QuoteDao;
import com.mns.alphaposition.server.engine.portfolio.Portfolio;
import com.mns.alphaposition.server.engine.strategy.MomentumStrategy;
import com.mns.alphaposition.server.engine.strategy.SimpleRankingStrategy;
import com.mns.alphaposition.shared.engine.model.Fund;
import com.mns.alphaposition.shared.engine.model.Quote;
import com.mns.alphaposition.shared.params.MomentumStrategyParams;
import com.mns.alphaposition.shared.params.PortfolioParams;
import com.mns.alphaposition.shared.params.RankingStrategyParams;
import com.mns.alphaposition.shared.params.SimpleRankingStrategyParams;
import com.mns.alphaposition.shared.util.QuoteUtils;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MomentumStrategyTests {

    private final PortfolioParams portfolioParams =
            new PortfolioParams(new BigDecimal("1000"), new BigDecimal("12.95"));
    private final Portfolio portfolio = new Portfolio(portfolioParams);
    private final Executor executor = new Executor(portfolio, portfolioParams.getTransactionCost());

    private final RankingStrategyParams rankingStrategyParams = new SimpleRankingStrategyParams(10, 9);
    private final MomentumStrategyParams strategyParams = new MomentumStrategyParams(1, rankingStrategyParams, 3);

    private final LocalDate fromDate = new LocalDate(2009, 1, 1);
    private final LocalDate toDate = new LocalDate(2011, 1, 1);

    private final String[] fundStrs = {"iShares FTSE China 25 Index Fund,FXI,China Region,iShares,7030000000,0.0072,2004-10-05,15407400",
            "iShares MSCI Taiwan Index,EWT,China Region,iShares,3450000000,0.0071,2000-06-20,11161600",
            "iShares Silver Trust,SLV,Commodities Precious Metals,iShares,12350000000,0.005,2006-04-21,71429800",
            "iShares S&P Global Telecommunications,IXP,Communications,iShares,413300000,0.0048,2001-11-12,39416",
            "iShares Dow Jones US Consumer Services,IYC,Consumer Discretionary,iShares,247920000,0.0047,2000-06-12,38952"};
    private final List<Fund> funds = loadAList(fundStrs);

    private final QuoteDao quoteDao = new QuoteDao(ObjectifyService.factory());

    private final LocalDatastoreServiceTestConfig config = new LocalDatastoreServiceTestConfig();
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(config);

    long time = 0L;

    @Before
    public void setUp() {
        helper.setUp();
        quoteDao.put(QuoteSet.getQuotes());
        time = System.currentTimeMillis();
        for (Fund fund : funds) {
            List<Quote> quotes = quoteDao.query(fund);
            List<Quote> missingQuotes = QuoteUtils.getMissingQuotes(fund.getInceptionDate(),
                    new LocalDate("2011-06-24"), quotes);
            quoteDao.put(missingQuotes);
        }
        System.out.println(System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
    }

    @Test
    public void testMomentumStrategy() {
        SimpleRankingStrategy rankingStrategy = new SimpleRankingStrategy(quoteDao);
        MomentumStrategy tradingStrategy = new MomentumStrategy(rankingStrategy, executor, portfolio);
        time = System.currentTimeMillis();
        tradingStrategy.execute(fromDate, toDate, funds, strategyParams);
        System.out.println(System.currentTimeMillis() - time);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    private static List<Fund> loadAList(String[] someStrs) {
        List<Fund> funds1 = new ArrayList<Fund>();
        for (String str : someStrs) {
            String[] split = str.split(",");
            funds1.add(new Fund(split[1], split[0], split[2], split[3], new BigDecimal(split[4]),
                    new BigDecimal(split[5]), new LocalDate(split[6]), new BigDecimal(split[7])));
        }
        return funds1;
    }


}
