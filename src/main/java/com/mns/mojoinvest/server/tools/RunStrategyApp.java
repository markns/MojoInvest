package com.mns.mojoinvest.server.tools;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mns.mojoinvest.server.engine.Strategy2ResultBuilder;
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
import com.mns.mojoinvest.shared.params.BacktestParams;
import com.mns.mojoinvest.shared.params.PortfolioParams;
import com.mns.mojoinvest.shared.params.Strategy2Params;
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

    private final Strategy2ResultBuilder resultBuilder;

    @Inject
    public RunStrategyApp(QuoteDao quoteDao, FundDao fundDao, CalculatedValueDao calculatedValueDao,
                          PortfolioFactory portfolioFactory, MomentumStrategy2 strategy2,
                          Strategy2ResultBuilder resultBuilder) {
        this.quoteDao = quoteDao;
        this.fundDao = fundDao;
        this.calculatedValueDao = calculatedValueDao;
        this.portfolioFactory = portfolioFactory;
        this.strategy2 = strategy2;
        this.resultBuilder = resultBuilder;
    }

    public void initDaos() {
        log.info("Loading in-memory dao's");
//        ((InMemoryQuoteDao) quoteDao).init("data/etf_international_quotes.csv", "data/etf_quotes_compare.csv");
//        ((InMemoryFundDao) fundDao).init("data/etf_international_funds.csv");
//        ((InMemoryCalculatedValueDao) calculatedValueDao).init("data/etf_international_cvs.csv");
        ((InMemoryQuoteDao) quoteDao).init("data/etf_sector_quotes.csv", "data/etf_quotes_compare.csv");
        ((InMemoryFundDao) fundDao).init("data/etf_sector_funds.csv");
        ((InMemoryCalculatedValueDao) calculatedValueDao).init("data/etf_sector_cvs.csv");
//        ((InMemoryQuoteDao) quoteDao).init("data/etf_asset_alloc_quotes.csv", "data/etf_quotes_compare.csv");
//        ((InMemoryFundDao) fundDao).init("data/etf_asset_alloc_funds.csv");
//        ((InMemoryCalculatedValueDao) calculatedValueDao).init("data/etf_asset_alloc_cvs.csv");
//        ((InMemoryQuoteDao) quoteDao).init("data/ishares_quotes.csv", "data/ishares_quotes_missing.csv", "data/etf_quotes_compare.csv");
//        ((InMemoryFundDao) fundDao).init("data/ishares_funds.csv");
//        ((InMemoryCalculatedValueDao) calculatedValueDao).init("data/ishares_cvs.csv");
//        ((InMemoryQuoteDao) quoteDao).init("data/fidelity_quotes.csv", "data/fidelity_quotes_missing.csv", "data/etf_quotes_compare.csv");
//        ((InMemoryFundDao) fundDao).init("data/fidelity_funds.csv");
//        ((InMemoryCalculatedValueDao) calculatedValueDao).init("data/fidelity_cvs.csv");
    }

    private void run() {

        LocalDate fDate = new LocalDate("1990-01-01");
//        LocalDate tDate = new LocalDate("2007-12-31");
        LocalDate tDate = new LocalDate("2012-03-01");
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
        boolean equityCurveTrading = false;
        int equityCurveWindow = 52;
        boolean useSafeAsset = false;
        String safeAsset = "FSUTX";
//        String safeAsset = "GSPC";
        String relativeStrengthStyle = "MA";

        String funds = "IUSA|IEEM|IWRD|EUE|ISF|IBCX|INAA|IJPN|IFFF|IWDP|SEMB|IMEU|BRIC|FXC|IGLT|IBZL|IKOR|IEUX|MIDD|EUN|LTAM|ITWN|IEER|IPXJ|IEMS|ISP6|SSAM|SAUS|SRSA|RUSS|NFTY";
        funds = null;
        Collection<Fund> universe;
        if (funds != null) {
            universe = fundDao.get(toList(Splitter.on("|").split(funds)));
        } else {
            universe = fundDao.getAll();
        }
        Portfolio portfolio = portfolioFactory.create(new PortfolioParams(cash, transactionCost, fromDate), false);
        Portfolio shadowPortfolio = portfolioFactory.create(new PortfolioParams(cash, transactionCost, fromDate), true);

        BacktestParams params = new BacktestParams(fromDate, toDate);

        Strategy2Params strategyParams = new Strategy2Params(portfolioSize, holdingPeriod, ma1, ma2, roc, alpha,
                castOff, stddev, equityCurveTrading, equityCurveWindow, relativeStrengthStyle, useSafeAsset, safeAsset);

        try {
            strategy2.execute(portfolio, shadowPortfolio, params, universe, strategyParams);
            //Should we use assisted inject here?
            resultBuilder.setPortfolio(portfolio)
                    .setShadowPortfolio(shadowPortfolio)
                    .setBacktestParams(params)
                    .setStrategyParams(strategyParams)
                    .setUniverse(universe)
                    .build();
        } catch (StrategyException e) {
            e.printStackTrace();
        }
    }

    private static <E> List<E> toList(Iterable<E> iterable) {
        return (iterable instanceof List)
                ? (List<E>) iterable
                : Lists.newArrayList(iterable.iterator());
    }

    //Fidelity
//    [INFO|main|10:49:07]: Params: {portfolioSize=3, rebalanceFrequency=1, ma1=12, ma2=26, roc=26, castOff=17, stddev=26, equityCurveTrading=false, equityCurveWindow=50, relativeStrengthStyle=ROC}
//    [INFO|main|10:49:08]: Number of trades: 139
//            [INFO|main|10:49:08]: Final portfolio value: 222868.34
//            [INFO|main|10:49:08]: CAGR: 20.031978154377427%

    //International
//    [INFO|main|6:55:17]: Params: {portfolioSize=1, rebalanceFrequency=1, ma1=26, ma2=39, roc=39, castOff=9, stddev=26, equityCurveTrading=true, equityCurveWindow=50, relativeStrengthStyle=ROC}
//    [INFO|main|6:55:17]: Number of trades: 34
//            [INFO|main|6:55:17]: Final portfolio value: 108830.36
//            [INFO|main|6:55:18]: CAGR: 11.461530717275558%

    //Sector
    //INFO: Params: {portfolioSize=1, rebalanceFrequency=1, ma1=26, ma2=39, roc=39, castOff=8, stddev=26, equityCurveTrading=true, equityCurveWindow=60, relativeStrengthStyle=ROC}
    //INFO: Number of trades: 31
    //INFO: Final portfolio value: 18955.24

    //Asset alloc
    //INFO: Params: {portfolioSize=1, rebalanceFrequency=1, ma1=26, ma2=39, roc=39, castOff=8, stddev=26, equityCurveTrading=true, equityCurveWindow=60, relativeStrengthStyle=ROC}
    //INFO: Number of trades: 16
    //INFO: Final portfolio value: 20435.96

//    [INFO|main|11:02:07]: Params: Params: {portfolioSize=1, rebalanceFrequency=1, ma1=12, ma2=26, roc=26, castOff=5, stddev=26, equityCurveTrading=false, equityCurveWindow=52, relativeStrengthStyle='MA', useSafeAsset=true, safeAsset='IBTS'}
//    [INFO|main|11:02:07]: Number of trades: 47
//    [INFO|main|11:02:07]: MaxDD: 30.8932800%
//    [INFO|main|11:02:07]: Final portfolio value: 42368.604428
//    [INFO|main|11:02:08]: CAGR: 6.782972784524954%


}

