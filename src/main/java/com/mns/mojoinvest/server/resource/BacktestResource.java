package com.mns.mojoinvest.server.resource;

import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.params.Params;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioFactory;
import com.mns.mojoinvest.server.engine.result.ResultBuilderException;
import com.mns.mojoinvest.server.engine.result.StrategyResult;
import com.mns.mojoinvest.server.engine.result.StrategyResultBuilder;
import com.mns.mojoinvest.server.engine.strategy.MomentumStrategy;
import com.mns.mojoinvest.server.engine.strategy.StrategyException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

@Path("/api/backtest")
public class BacktestResource {

    private static final Logger log = Logger.getLogger(BacktestResource.class.getName());

    private final FundDao fundDao;
    private final QuoteDao quoteDate;
    private final CalculatedValueDao calculatedValueDao;
    private final MomentumStrategy strategy;
    private final PortfolioFactory portfolioFactory;

    private final StrategyResultBuilder resultBuilder;


    @Inject
    public BacktestResource(final FundDao fundDao, final QuoteDao quoteDao,
                            final CalculatedValueDao calculatedValueDao,
                            PortfolioFactory portfolioFactory, MomentumStrategy strategy,
                            StrategyResultBuilder resultBuilder) {
        this.fundDao = fundDao;
        this.quoteDate = quoteDao;
        this.calculatedValueDao = calculatedValueDao;
        this.portfolioFactory = portfolioFactory;
        this.strategy = strategy;
        this.resultBuilder = resultBuilder;
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public StrategyResult runBacktest(Params params)
            throws StrategyException, ResultBuilderException {
        log.info("Run backtest called with " + params);
        long start = System.currentTimeMillis();
        Collection<Fund> universe;
        if (params.getUniverse() != null) {
            universe = fundDao.get(params.getUniverse());
        } else {
            universe = fundDao.list();
        }
        Portfolio portfolio = portfolioFactory.create(params, false);

        long step = System.currentTimeMillis();
        Map<String, Object> additionalResults =
                strategy.execute(portfolio, params, universe);
        log.info("Total run strategy time: " + (System.currentTimeMillis() - step));
        //Should we use assisted inject here?
        step = System.currentTimeMillis();
        StrategyResult result = resultBuilder.setPortfolio(portfolio)
                .setAdditionalResults(additionalResults)
                .setParams(params)
                .build();
        log.info("Total result builder time: " + (System.currentTimeMillis() - step));
        log.info("Total request time: " + (System.currentTimeMillis() - start));
        return result;
    }

}
