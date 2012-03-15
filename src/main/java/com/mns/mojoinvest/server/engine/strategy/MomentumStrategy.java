package com.mns.mojoinvest.server.engine.strategy;

import com.google.inject.Inject;
import com.googlecode.objectify.NotFoundException;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Ranking;
import com.mns.mojoinvest.server.engine.model.RankingParams;
import com.mns.mojoinvest.server.engine.model.dao.RankingDao;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioException;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import com.mns.mojoinvest.shared.params.BacktestParams;
import com.mns.mojoinvest.shared.params.MomentumStrategyParams;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class MomentumStrategy {

    private static final Logger log = Logger.getLogger(MomentumStrategy.class.getName());

    private final Executor executor;
    private final RankingDao rankingDao;

    @Inject
    public MomentumStrategy(Executor executor, RankingDao rankingDao) {
        this.executor = executor;
        this.rankingDao = rankingDao;
    }

    public void execute(Portfolio portfolio, BacktestParams backtestParams,
                        Set<String> acceptableFunds, MomentumStrategyParams strategyParams)
            throws StrategyException {

        LocalDate fromDate = new LocalDate(backtestParams.getFromDate());
        LocalDate toDate = new LocalDate(backtestParams.getToDate());

        if (fromDate.isAfter(toDate))
            throw new StrategyException("From date cannot be after to date");

        List<LocalDate> rebalanceDates = getRebalanceDates(fromDate, toDate, strategyParams);
        log.info("Loading rankings for " + rebalanceDates.size() + " rebalance dates");
        List<Ranking> rankings = rankingDao.get(rebalanceDates, new RankingParams(strategyParams.getFormationPeriod()));

        log.info("Starting rebalancing");
        for (int i = 0; i < rebalanceDates.size(); i++) {
            try {
                Set<String> selection = getSelection(rankings.get(i).getSymbols(), acceptableFunds, strategyParams);
                log.info("Rebalance date: " + rebalanceDates.get(i) + ", Selection: " + selection);
//                sellLosers(portfolio, rebalanceDates.get(i), selection);
//                buyWinners(portfolio, strategyParams, rebalanceDates.get(i), selection);
            } catch (NotFoundException e) {
                //TODO: How should we handle exceptions here - what type of exceptions are they?
                log.info(rebalanceDates.get(i) + " " + e.getMessage());
            } catch (StrategyException e) {
                log.info(rebalanceDates.get(i) + " " + e.getMessage());
            }
        }
    }

    private Set<String> getSelection(List<String> ranked, Set<String> acceptableFunds,
                                     MomentumStrategyParams params) throws StrategyException {
        ranked.retainAll(acceptableFunds);
        if (ranked.size() <= params.getPortfolioSize() * 2)
            throw new StrategyException("Not enough funds in population to make selection");
        Set<String> selection = new HashSet<String>();
        for (String symbol : ranked) {
            if (selection.size() < params.getPortfolioSize()) {
                selection.add(symbol);
            } else {
                break;
            }
        }
        return selection;
    }


    private void sellLosers(Portfolio portfolio, LocalDate rebalanceDate, Collection<Fund> selection)
            throws StrategyException {
        for (Fund fund : portfolio.getActiveFunds(rebalanceDate)) {
            if (!selection.contains(fund)) {
                try {
                    executor.sellAll(portfolio, fund, rebalanceDate);
                } catch (PortfolioException e) {
                    e.printStackTrace();
                    throw new StrategyException("Unable to sell losers", e);
                }
            }
        }
    }

    private void buyWinners(Portfolio portfolio, MomentumStrategyParams params, LocalDate rebalanceDate,
                            Collection<Fund> selection) throws StrategyException {

        BigDecimal numEmpty = new BigDecimal(params.getPortfolioSize() - portfolio.openPositionCount(rebalanceDate));
        BigDecimal availableCash = portfolio.getCash(rebalanceDate).
                subtract(portfolio.getTransactionCost().
                        multiply(numEmpty));
        for (Fund fund : selection) {
            if (!portfolio.contains(fund, rebalanceDate)) {
                BigDecimal allocation = availableCash
                        .divide(numEmpty, MathContext.DECIMAL32);
                try {
                    executor.buy(portfolio, fund, rebalanceDate, allocation);
                } catch (PortfolioException e) {
                    e.printStackTrace();
                    throw new StrategyException("Unable to buy winners", e);
                }
            }
        }
    }

    private List<LocalDate> getRebalanceDates(LocalDate fromDate, LocalDate toDate, MomentumStrategyParams params) {
        return TradingDayUtils.getMonthlySeries(fromDate, toDate, params.getHoldingPeriod(), true);
    }

}
