package com.mns.mojoinvest.server.handler;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.portfolio.Lot;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioFactory;
import com.mns.mojoinvest.server.engine.portfolio.Position;
import com.mns.mojoinvest.server.engine.result.StrategyResultBuilder;
import com.mns.mojoinvest.server.engine.strategy.MomentumStrategy;
import com.mns.mojoinvest.server.engine.strategy.StrategyException;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.shared.dispatch.RunStrategyAction;
import com.mns.mojoinvest.shared.dispatch.RunStrategyResult;
import com.mns.mojoinvest.shared.dto.StrategyResult;
import com.mns.mojoinvest.shared.params.BacktestParams;
import com.mns.mojoinvest.shared.params.FundFilter;
import com.mns.mojoinvest.shared.params.Params;
import org.joda.time.LocalDate;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class RunStrategyHandler implements
        ActionHandler<RunStrategyAction, RunStrategyResult> {

    private static final Logger log = Logger.getLogger(RunStrategyHandler.class.getName());

    private MomentumStrategy strategy;
    private final PortfolioFactory portfolioFactory;
    private final StrategyResultBuilder strategyResultBuilder;
    private final FundDao fundDao;

    @Inject
    public RunStrategyHandler(MomentumStrategy strategy,
                              PortfolioFactory portfolioFactory,
                              StrategyResultBuilder strategyResultBuilder,
                              FundDao fundDao) {
        this.strategy = strategy;
        this.portfolioFactory = portfolioFactory;
        this.strategyResultBuilder = strategyResultBuilder;
        this.fundDao = fundDao;
    }

    @Override
    public RunStrategyResult execute(final RunStrategyAction action,
                                     final ExecutionContext context) throws ActionException {

        Params params = action.getParams();

        //TODO: Total hack - sort this out
        params.getPortfolioParams().setCreationDate(params.getBacktestParams().getFromDate());

        //TODO: Does the portfolioFactory need to be synchronised?
        Portfolio portfolio = portfolioFactory.create(params.getPortfolioParams());

        //TODO: Abstract the getAcceptableFunds call to a separate class - why is it so slow!
        log.info("Getting set of acceptable funds");
        Set<String> funds = getAcceptableFunds(params.getFundFilter());

        StrategyResult result;
        try {
            BacktestParams backtestParams = params.getBacktestParams();
            log.info("Running strategy with " + params);
            strategy.execute(portfolio, backtestParams, funds, params.getStrategyParams());
//            log.info(logPortfolio(portfolio));
            log.info("Starting build of strategy result set");
            result = strategyResultBuilder.build(portfolio, new LocalDate(backtestParams.getFromDate()),
                    //TODO: try to implement a getToLocalDate() method in backtestParams
                    new LocalDate(backtestParams.getToDate()));
        } catch (StrategyException e) {
            e.printStackTrace();
            log.severe("Error running strategy with " + params);
            throw new ActionException(e);
        }

        return new RunStrategyResult("", result);
    }

    private String logPortfolio(Portfolio portfolio) {
        StringWriter stringWriter = new StringWriter();
        CSVWriter writer = new CSVWriter(stringWriter);
        for (Position position : portfolio.getPositions()) {
            writer.writeNext(position.getFund().toStrArr());
            for (Lot lot : position.getLots()) {
                writer.writeNext(lot.getOpeningTransaction().toStrArr());
                for (SellTransaction sellTransaction : lot.getSellTransactions()) {
                    writer.writeNext(sellTransaction.toStrArr());
                }
            }
        }
        return stringWriter.toString();
    }

    private Set<String> getAcceptableFunds(FundFilter fundFilter) {
        Set<String> funds = new HashSet<String>();
        for (String category : fundFilter.getCategories()) {
            funds.addAll(fundDao.getByCategory(category));
        }
        for (String provider : fundFilter.getProviders()) {
            funds.addAll(fundDao.getByProvider(provider));
        }
        return funds;
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
