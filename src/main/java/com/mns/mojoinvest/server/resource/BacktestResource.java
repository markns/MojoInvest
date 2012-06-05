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
import org.joda.time.LocalDate;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

@Path("/backtest")
public class BacktestResource {

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
        System.out.println(params);
        Collection<Fund> universe;
        if (params.getUniverse() != null) {
            universe = fundDao.get(params.getUniverse());
        } else {
            universe = fundDao.getAll();
        }
        Portfolio portfolio = portfolioFactory.create(params, false);
        Portfolio shadowPortfolio = portfolioFactory.create(params, true);

        Map<String, Map<LocalDate, BigDecimal>> additionalResults =
                strategy.execute(portfolio, shadowPortfolio, params, universe);
        //Should we use assisted inject here?
        return resultBuilder.setPortfolio(portfolio)
                .setShadowPortfolio(shadowPortfolio)
                .setAdditionalResults(additionalResults)
                .setParams(params)
                .build();
    }

}
