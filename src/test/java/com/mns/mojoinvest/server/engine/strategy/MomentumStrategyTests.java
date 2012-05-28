package com.mns.mojoinvest.server.engine.strategy;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Sets;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.ObjectifyFundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.params.BacktestParams;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.argThat;


@RunWith(MockitoJUnitRunner.class)
public class MomentumStrategyTests {

    @Mock
    private QuoteDao quoteDao;

    //    private RankingDao rankingDao = new RankingDao(ObjectifyService.factory());
    private FundDao fundDao = new ObjectifyFundDao(ObjectifyService.factory());

    private final LocalDatastoreServiceTestConfig config = new LocalDatastoreServiceTestConfig();
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(config);

    private final Quote dummyQuote = new Quote("DUMMY", new LocalDate("2000-01-15"),
            BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
            BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, false);

    Map<String, Fund> funds = new HashMap<String, Fund>() {
        {
            put("A", new Fund("A", "A", "", "", true, "", "", "", new LocalDate("2001-01-01")));
            put("B", new Fund("B", "B", "", "", true, "", "", "", new LocalDate("2001-01-01")));
            put("C", new Fund("C", "C", "", "", true, "", "", "", new LocalDate("2001-01-01")));
            put("D", new Fund("D", "D", "", "", true, "", "", "", new LocalDate("2001-01-01")));
            put("E", new Fund("E", "E", "", "", true, "", "", "", new LocalDate("2001-01-01")));
            put("F", new Fund("F", "F", "", "", true, "", "", "", new LocalDate("2001-01-01")));
            put("G", new Fund("G", "G", "", "", true, "", "", "", new LocalDate("2001-01-01")));
            put("H", new Fund("H", "H", "", "", true, "", "", "", new LocalDate("2001-01-01")));
            put("I", new Fund("I", "I", "", "", true, "", "", "", new LocalDate("2001-01-01")));
            put("J", new Fund("J", "J", "", "", true, "", "", "", new LocalDate("2001-01-01")));
        }
    };

    Set<String> acceptable = Sets.newHashSet("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");

    //    RankingParams rankingParams = new RankingParams(9);
//    Collection<Ranking> rankings = Arrays.asList(
//            new Ranking(new LocalDate("2011-09-01"), rankingParams, "A|B|C|D|E|F|G|H|I|J"),
//            new Ranking(new LocalDate("2011-09-30"), rankingParams, "A|B|C|D|E|F|G|H|I|J"),
//            new Ranking(new LocalDate("2011-11-01"), rankingParams, "C|D|E|F|G|H|I|J|A|B"),
//            new Ranking(new LocalDate("2011-12-01"), rankingParams, "H|I|J|A|B|C|D|E|F|G"),
//            new Ranking(new LocalDate("2011-12-30"), rankingParams, "J|A|B|C|D|E|F|G|H|I")
//    );
//
//    private OldMomentumStrategy strategy;
    private Portfolio portfolio;
    private BacktestParams backtestParams;
//    private MomentumStrategyParams strategyParams;

    @Before
    public void setUp() {
        helper.setUp();
        fundDao.put(new HashSet<Fund>(funds.values()));
//        rankingDao.put(rankings);
//        Executor executor = new NextTradingDayExecutor(quoteDao);
//        when(quoteDao.get(anyFund(), anyLocalDate())).thenReturn(dummyQuote);
//        strategy = new OldMomentumStrategy(executor, rankingDao, quoteDao, fundDao);
//
//        portfolio = new SimplePortfolio(fundDao, quoteDao, new PortfolioParams(10000.0, 12.95,
//                new LocalDate("2011-01-01").toDateMidnight().toDate()), false);
//        backtestParams = new BacktestParams(new LocalDate("2011-09-01").toDateMidnight().toDate(),
//                new LocalDate("2012-01-01").toDateMidnight().toDate());
//        strategyParams = new MomentumStrategyParams(9, 1, 3);
    }

    @Test
    public void testStrategy() throws StrategyException {
//        strategy.execute(portfolio, backtestParams, acceptable, strategyParams);
    }

    //TODO: Move to shared class
    public static LocalDate anyLocalDate() {
        return argThat(new IsLocalDate());
    }

    public static Fund anyFund() {
        return argThat(new IsFund());
    }

    public static class IsFund extends ArgumentMatcher<Fund> {
        @Override
        public boolean matches(Object argument) {
            return argument instanceof Fund;
        }
    }

    public static class IsLocalDate extends ArgumentMatcher<LocalDate> {
        @Override
        public boolean matches(Object argument) {
            return argument instanceof LocalDate;
        }
    }

}
