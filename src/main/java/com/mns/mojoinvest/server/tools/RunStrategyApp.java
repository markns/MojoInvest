package com.mns.mojoinvest.server.tools;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.*;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioFactory;
import com.mns.mojoinvest.server.engine.strategy.MomentumStrategy2;
import com.mns.mojoinvest.server.engine.strategy.StrategyException;
import com.mns.mojoinvest.server.guice.DispatchServletModule;
import com.mns.mojoinvest.server.guice.EngineModule;
import com.mns.mojoinvest.server.guice.StandaloneModule;
import com.mns.mojoinvest.server.guice.TradingStrategyModule;
import com.mns.mojoinvest.server.servlet.StrategyServlet;
import com.mns.mojoinvest.shared.params.BacktestParams;
import com.mns.mojoinvest.shared.params.PortfolioParams;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class RunStrategyApp {

    private static final Logger log = Logger.getLogger(RunStrategyApp.class.getName());

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new DispatchServletModule(),
                new StandaloneModule(),
                new EngineModule(),
                new TradingStrategyModule());

        RunStrategyApp app = injector.getInstance(RunStrategyApp.class);
        app.initDaos();
        app.run();
    }

    private final QuoteDao quoteDao;
    private final CalculatedValueDao calculatedValueDao;
    private final FundDao fundDao;
    private MomentumStrategy2 strategy2;
    private final PortfolioFactory portfolioFactory;


    @Inject
    public RunStrategyApp(QuoteDao quoteDao, FundDao fundDao, CalculatedValueDao calculatedValueDao,
                          PortfolioFactory portfolioFactory, MomentumStrategy2 strategy2) {
        this.quoteDao = quoteDao;
        this.fundDao = fundDao;
        this.calculatedValueDao = calculatedValueDao;
        this.portfolioFactory = portfolioFactory;
        this.strategy2 = strategy2;
    }

    public void initDaos() {
        log.info("Loading in-memory dao's");
        ((InMemoryQuoteDao) quoteDao).init("data/etf_international_quotes.csv");
        ((InMemoryFundDao) fundDao).init("data/etf_international_funds.csv");
        ((InMemoryCalculatedValueDao) calculatedValueDao).init("data/etf_international_roc.csv", "data/etf_international_stddev.csv");
    }

    private void run() {

        LocalDate fDate = new LocalDate("2000-01-01");
        LocalDate tDate = new LocalDate("2012-03-22");
        Date fromDate = fDate.toDateMidnight().toDate();
        Date toDate = tDate.toDateMidnight().toDate();

        double cash = 10000d;
        double transactionCost = 10d;
        int portfolioSize = 3;
        int holdingPeriod = 1;
        int ma1 = 12;
        int ma2 = 26;
        int roc = 26;
        int castOff = 5;
        int stddev = 26;
        boolean equityCurveTrading = true;
        int equityCurveWindow = 50;

        String funds = null;
        Collection<Fund> universe;
        if (funds != null) {
            universe = fundDao.get(toList(Splitter.on("|").split(funds)));
        } else {
            universe = fundDao.getAll();
        }
        Portfolio portfolio = portfolioFactory.create(new PortfolioParams(cash, transactionCost, fromDate));

        BacktestParams params = new BacktestParams(fromDate, toDate);

        StrategyServlet.Strategy2Params strategyParams = new StrategyServlet.Strategy2Params(portfolioSize, holdingPeriod, ma1, ma2, roc,
                castOff, stddev, equityCurveTrading, equityCurveWindow);

        try {
            strategy2.execute(portfolio, params, universe, strategyParams);
        } catch (StrategyException e) {
            e.printStackTrace();
        }
    }

    private static <E> List<E> toList(Iterable<E> iterable) {
        return (iterable instanceof List)
                ? (List<E>) iterable
                : Lists.newArrayList(iterable.iterator());
    }

}


