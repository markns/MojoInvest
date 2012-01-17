package com.mns.mojoinvest.server.engine.strategy;

import com.google.inject.Inject;
import com.googlecode.objectify.NotFoundException;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Ranking;
import com.mns.mojoinvest.server.engine.model.RankingParams;
import com.mns.mojoinvest.server.engine.model.dao.CorrelationDao;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.RankingDao;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioException;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import com.mns.mojoinvest.shared.params.BacktestParams;
import com.mns.mojoinvest.shared.params.MomentumStrategyParams;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class MomentumStrategy {

    private static final Logger log = Logger.getLogger(MomentumStrategy.class.getName());

    private final Executor executor;
    private final RankingDao rankingDao;
    private final FundDao fundDao;
    private final CorrelationDao correlationDao;

    @Inject
    public MomentumStrategy(Executor executor,
                            RankingDao rankingDao,
                            FundDao fundDao,
                            CorrelationDao correlationDao) {
        this.executor = executor;
        this.rankingDao = rankingDao;
        this.fundDao = fundDao;
        this.correlationDao = correlationDao;
    }

    public void execute(Portfolio portfolio, BacktestParams backtestParams,
                        Set<Fund> acceptableFunds, MomentumStrategyParams strategyParams)
            throws StrategyException {

        LocalDate fromDate = new LocalDate(backtestParams.getFromDate());
        LocalDate toDate = new LocalDate(backtestParams.getToDate());

        if (fromDate.isAfter(toDate))
            throw new StrategyException("From date cannot be after to date");

        List<LocalDate> rebalanceDates = getRebalanceDates(fromDate, toDate, strategyParams);

        for (LocalDate rebalanceDate : rebalanceDates) {
            try {
                Ranking ranking = rankingDao.get(rebalanceDate,
                        new RankingParams(strategyParams.getFormationPeriod()));
                Collection<Fund> selection = getSelection(ranking.getSymbols(),
                        acceptableFunds, strategyParams);
                sellLosers(portfolio, rebalanceDate, selection);
                buyWinners(portfolio, strategyParams, rebalanceDate, selection);

                System.out.println(portfolio.getActiveFunds(rebalanceDate));
            } catch (NotFoundException e) {
                //TODO: How should we handle exceptions here - what type of exceptions are they?
                log.info(rebalanceDate + " " + e.getMessage());
            } catch (StrategyException e) {
                log.info(rebalanceDate + " " + e.getMessage());
            }
        }
    }

    private Collection<Fund> getSelection(List<String> ranked, Set<Fund> acceptableFunds,
                                          MomentumStrategyParams params) throws StrategyException {

        List<String> acceptableSymbols = new ArrayList<String>(acceptableFunds.size());
        for (Fund fund : acceptableFunds) {
            acceptableSymbols.add(fund.getSymbol());
        }
        ranked.retainAll(acceptableSymbols);
        if (ranked.size() <= params.getPortfolioSize() * 2)
            throw new StrategyException("Not enough funds in population to make selection");

        List<String> selection = new ArrayList<String>();
        for (String symbol : ranked) {

            if (selection.size() < params.getPortfolioSize()) {

                //Check correlation to funds already in selection here
                boolean tooCorrelated = false;
                for (String selected : selection) {
                    double correl = correlationDao.getCorrelation(selected, symbol);
                    if (correl > 0.95) {
                        tooCorrelated = true;
                        break;
                    }
                }
                if (tooCorrelated) {
                    continue;
                }

                //If something is already in the portfolio that is correlated to the selection
                //swap it in here

                selection.add(symbol);
            } else {
                break;
            }

        }
        return fundDao.get(selection);
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
