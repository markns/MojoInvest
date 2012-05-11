package com.mns.mojoinvest.server.tools;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mns.mojoinvest.server.engine.ResultBuilderException;
import com.mns.mojoinvest.server.engine.Strategy2ResultBuilder;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.*;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioFactory;
import com.mns.mojoinvest.server.engine.strategy.MomentumStrategy;
import com.mns.mojoinvest.server.engine.strategy.StrategyException;
import com.mns.mojoinvest.server.guice.DispatchServletModule;
import com.mns.mojoinvest.server.guice.EngineModule;
import com.mns.mojoinvest.server.guice.StandaloneModule;
import com.mns.mojoinvest.server.guice.TradingStrategyModule;
import com.mns.mojoinvest.shared.params.BacktestParams;
import com.mns.mojoinvest.shared.params.PortfolioParams;
import com.mns.mojoinvest.shared.params.StrategyParams;
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
    private final MomentumStrategy strategy;
    private final PortfolioFactory portfolioFactory;

    private final Strategy2ResultBuilder resultBuilder;

    @Inject
    public RunStrategyApp(QuoteDao quoteDao, FundDao fundDao, CalculatedValueDao calculatedValueDao,
                          PortfolioFactory portfolioFactory, MomentumStrategy strategy,
                          Strategy2ResultBuilder resultBuilder) {
        this.quoteDao = quoteDao;
        this.fundDao = fundDao;
        this.calculatedValueDao = calculatedValueDao;
        this.portfolioFactory = portfolioFactory;
        this.strategy = strategy;
        this.resultBuilder = resultBuilder;
    }

    public void initDaos() {
        log.info("Loading in-memory dao's");
//        ((InMemoryQuoteDao) quoteDao).init("data/etf_international_quotes.csv", "data/etf_quotes_compare.csv");
//        ((InMemoryFundDao) fundDao).init("data/etf_international_funds.csv");
//        ((InMemoryCalculatedValueDao) calculatedValueDao).init("data/etf_international_cvs.csv");
//        ((InMemoryQuoteDao) quoteDao).init("data/etf_sector_quotes.csv", "data/etf_quotes_compare.csv");
//        ((InMemoryFundDao) fundDao).init("data/etf_sector_funds.csv");
//        ((InMemoryCalculatedValueDao) calculatedValueDao).init("data/etf_sector_cvs.csv");
//        ((InMemoryQuoteDao) quoteDao).init("data/etf_asset_alloc_quotes.csv", "data/etf_quotes_compare.csv");
//        ((InMemoryFundDao) fundDao).init("data/etf_asset_alloc_funds.csv");
//        ((InMemoryCalculatedValueDao) calculatedValueDao).init("data/etf_asset_alloc_cvs.csv");
        ((InMemoryQuoteDao) quoteDao).init("data/ishares_quotes.csv", "data/ishares_quotes_missing.csv", "data/etf_quotes_compare.csv");
        ((InMemoryFundDao) fundDao).init("data/ishares_funds.csv");
        ((InMemoryCalculatedValueDao) calculatedValueDao).init("data/ishares_cvs.csv");
//        ((InMemoryQuoteDao) quoteDao).init("data/fidelity_quotes.csv", "data/fidelity_quotes_missing.csv", "data/etf_quotes_compare.csv");
//        ((InMemoryFundDao) fundDao).init("data/fidelity_funds.csv");
//        ((InMemoryCalculatedValueDao) calculatedValueDao).init("data/fidelity_cvs.csv");
    }

    private void run() {

        LocalDate fDate = new LocalDate("1990-01-01");
//        LocalDate fDate = new LocalDate("2000-01-01");
//        LocalDate tDate = new LocalDate("2007-12-31");
        LocalDate tDate = new LocalDate("2012-03-01");
//        LocalDate tDate = new LocalDate("2012-05-02");
        Date fromDate = fDate.toDateMidnight().toDate();
        Date toDate = tDate.toDateMidnight().toDate();

        double cash = 10000d;
        double transactionCost = 10d;
        int portfolioSize = 1;
        int holdingPeriod = 1;
        int ma1 = 12;
        int ma2 = 26;
        int roc = 26;
        int alpha = 100;
        int castOff = 8;
        int stddev = 26;
        boolean equityCurveTrading = true;
        int equityCurveWindow = 26;
        boolean useSafeAsset = false;
//        String safeAsset = "FSUTX"; //fidelity
        String safeAsset = "IGLT"; //ishares
//        String safeAsset = "GSPC";
        String relativeStrengthStyle = "MA";

        String funds = "IUSA|IEEM|IWRD|EUE|ISF|IBCX|INAA|IJPN|IFFF|IWDP|SEMB|IMEU|BRIC|FXC|IGLT|IBZL|IKOR|IEUX|MIDD|EUN|LTAM|ITWN|IEER|IPXJ|IEMS|ISP6|SSAM|SAUS|SRSA|RUSS|NFTY";
//        funds = null;
        Collection<Fund> universe;
        if (funds != null) {
            universe = fundDao.get(toList(Splitter.on("|").split(funds)));
        } else {
            universe = fundDao.getAll();
        }
        Portfolio portfolio = portfolioFactory.create(new PortfolioParams(cash, transactionCost, fromDate), false);
        Portfolio shadowPortfolio = portfolioFactory.create(new PortfolioParams(cash, transactionCost, fromDate), true);

        BacktestParams params = new BacktestParams(fromDate, toDate);

        StrategyParams strategyParams = new StrategyParams(portfolioSize, holdingPeriod, ma1, ma2, roc, alpha,
                castOff, stddev, equityCurveTrading, equityCurveWindow, relativeStrengthStyle, useSafeAsset, safeAsset);

        try {
            strategy.execute(portfolio, shadowPortfolio, params, universe, strategyParams);
            //Should we use assisted inject here?
            resultBuilder.setPortfolio(portfolio)
                    .setShadowPortfolio(shadowPortfolio)
                    .setBacktestParams(params)
                    .setStrategyParams(strategyParams)
                    .setUniverse(universe)
                    .build();
        } catch (StrategyException e) {
            e.printStackTrace();
        } catch (ResultBuilderException e) {
            e.printStackTrace();
        }
    }

    private static <E> List<E> toList(Iterable<E> iterable) {
        return (iterable instanceof List)
                ? (List<E>) iterable
                : Lists.newArrayList(iterable.iterator());
    }

}


