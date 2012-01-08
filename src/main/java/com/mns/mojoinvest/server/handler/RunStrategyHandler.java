package com.mns.mojoinvest.server.handler;

import com.google.inject.Inject;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioFactory;
import com.mns.mojoinvest.server.engine.strategy.MomentumStrategy;
import com.mns.mojoinvest.server.engine.strategy.StrategyException;
import com.mns.mojoinvest.shared.dispatch.RunStrategyAction;
import com.mns.mojoinvest.shared.dispatch.RunStrategyResult;
import com.mns.mojoinvest.shared.params.FundFilter;
import com.mns.mojoinvest.shared.params.Params;
import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RunStrategyHandler implements
        ActionHandler<RunStrategyAction, RunStrategyResult> {

    private MomentumStrategy strategy;

    private final PortfolioFactory portfolioFactory;

    private final FundDao fundDao;

    @Inject
    public RunStrategyHandler(MomentumStrategy strategy,
                              PortfolioFactory portfolioFactory,
                              FundDao fundDao) {
        this.strategy = strategy;
        this.portfolioFactory = portfolioFactory;
        this.fundDao = fundDao;
    }

    @Override
    public RunStrategyResult execute(final RunStrategyAction action,
                                     final ExecutionContext context) throws ActionException {

        Params params = action.getParams();

        //TODO: Does the portfolioFactory need to be synchronised?
        Portfolio portfolio = portfolioFactory.create(params.getPortfolioParams());

        Set<Fund> funds = getAcceptableFunds(params.getFundFilter());

        try {
            strategy.execute(portfolio,
                    new LocalDate(params.getBacktestParams().getFromDate()),
                    new LocalDate(params.getBacktestParams().getToDate()),
                    funds,
                    params.getStrategyParams());
        } catch (StrategyException e) {
            e.printStackTrace();
            throw new ActionException(e);
        }

        return new RunStrategyResult("");
    }

    private Set<Fund> getAcceptableFunds(FundFilter fundFilter) {
        Map<String, Object> filter = new HashMap<String, Object>(2);
        if (fundFilter.getProviders().size() > 0)
            filter.put("provider in", fundFilter.getProviders());
        if (fundFilter.getCategories().size() > 0)
            filter.put("category in", fundFilter.getCategories());
        return new HashSet<Fund>(fundDao.query(filter));
    }

    @Override
    public Class<RunStrategyAction> getActionType() {
        return RunStrategyAction.class;
    }

    @Override
    public void undo(final RunStrategyAction action,
                     final RunStrategyResult result, final ExecutionContext context)
            throws ActionException {
        // No undo
    }
}
