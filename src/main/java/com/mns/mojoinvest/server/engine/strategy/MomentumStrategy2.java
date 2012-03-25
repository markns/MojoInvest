package com.mns.mojoinvest.server.engine.strategy;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.Ranking;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioException;
import com.mns.mojoinvest.server.servlet.StrategyServlet;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import com.mns.mojoinvest.shared.params.BacktestParams;
import com.mns.mojoinvest.shared.params.MomentumStrategyParams;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.logging.Logger;

public class MomentumStrategy2 {

    private static final Logger log = Logger.getLogger(MomentumStrategy2.class.getName());

    private final Executor executor;
    private final QuoteDao quoteDao;
    private final FundDao fundDao;
    private final CalculatedValueDao calculatedValueDao;

    @Inject
    public MomentumStrategy2(Executor executor, QuoteDao quoteDao, FundDao fundDao, CalculatedValueDao calculatedValueDao) {
        this.executor = executor;
        this.quoteDao = quoteDao;
        this.fundDao = fundDao;
        this.calculatedValueDao = calculatedValueDao;
    }

    public void execute(Portfolio portfolio, BacktestParams backtestParams,
                        Collection<Fund> universe, StrategyServlet.Strategy2Params strategyParams)
            throws StrategyException {

        LocalDate fromDate = new LocalDate(backtestParams.getFromDate());
        LocalDate toDate = new LocalDate(backtestParams.getToDate());

        if (fromDate.isAfter(toDate))
            throw new StrategyException("From date cannot be after to date");

        List<LocalDate> rebalanceDates = getRebalanceDates(fromDate, toDate, strategyParams);

        for (LocalDate rebalanceDate : rebalanceDates) {
            Collection<CalculatedValue> ma1s = calculatedValueDao.get(rebalanceDate, universe, "SMA", strategyParams.getMa1());
            Collection<CalculatedValue> ma2s = calculatedValueDao.get(rebalanceDate, universe, "SMA", strategyParams.getMa2());
            System.out.println(ma1s);
            System.out.println(ma2s);

            if (ma1s.size() == ma2s.size()) {

            }

            //Calculate ma1s/ma2s
            //Divide by Std Dev
            //Rank order
        }

    }

    private void runRebalancing(Portfolio portfolio, MomentumStrategyParams strategyParams, List<LocalDate> rebalanceDates, List<List<String>> selections) throws StrategyException {
        log.info("Starting rebalancing");
        for (int i = 0; i < rebalanceDates.size(); i++) {
            try {
                List<String> selection = selections.get(i);
                log.fine("Rebalance date: " + rebalanceDates.get(i) + ", Selection: " + selection);
                sellLosers(portfolio, rebalanceDates.get(i), selection);
                buyWinners(portfolio, strategyParams, rebalanceDates.get(i), selection);
            } catch (NotFoundException e) {
                log.severe(rebalanceDates.get(i) + " " + e.getMessage());
                throw new StrategyException("Unable to find entity for rebalance date " + rebalanceDates.get(i), e);
            }
        }
    }

    private List<List<String>> getSelections(Set<String> acceptableFunds, MomentumStrategyParams strategyParams, List<LocalDate> rebalanceDates, List<Ranking> rankings) throws StrategyException {
        log.info("Getting selections from rankings and acceptable funds");
        List<List<String>> selections = new ArrayList<List<String>>();
        for (int i = 0; i < rebalanceDates.size(); i++) {
            selections.add(getSelection(rankings.get(i).getSymbols(),
                    acceptableFunds, strategyParams));
        }
        return selections;
    }

    private void cacheFundsAndQuotes(List<LocalDate> rebalanceDates, List<List<String>> selections) {
        log.info("Starting cache of funds and quotes");
        HashSet<String> funds = new HashSet<String>();
        List<Key<Quote>> quoteKeys = new ArrayList<Key<Quote>>();
        for (int i = 0; i < rebalanceDates.size(); i++) {
            List<String> selection = selections.get(i);
            for (String fund : selection) {
                funds.add(fund);
            }
            quoteKeys.addAll(quoteDao.getKeys(selection, rebalanceDates.get(i)));
        }
        log.info("Getting " + funds.size() + " funds");
        fundDao.get(funds);
        log.info("Getting " + quoteKeys.size() + " quotes");
        quoteDao.get(quoteKeys);
    }

    private List<String> getSelection(List<String> ranked, Set<String> acceptableFunds,
                                      MomentumStrategyParams params) throws StrategyException {
        ranked.retainAll(acceptableFunds);
        if (ranked.size() <= params.getPortfolioSize() * 2)
            throw new StrategyException("Not enough funds in population to make selection");
        return ranked.subList(0, params.getPortfolioSize());
    }

    private void sellLosers(Portfolio portfolio, LocalDate rebalanceDate, List<String> selection)
            throws StrategyException {
        for (String fund : portfolio.getActiveFunds(rebalanceDate)) {
            if (!selection.contains(fund)) {
                try {
                    executor.sellAll(portfolio, fund, rebalanceDate);
                } catch (PortfolioException e) {
                    e.printStackTrace();
                    throw new StrategyException("Unable to sell losers " + selection +
                            " on " + rebalanceDate +
                            ", current portfolio: " + portfolio.getActiveFunds(rebalanceDate) +
                            ", value: " + portfolio.marketValue(rebalanceDate), e);
                }
            }
        }
    }

    private void buyWinners(Portfolio portfolio, MomentumStrategyParams params, LocalDate rebalanceDate,
                            List<String> selection) throws StrategyException {

        BigDecimal numEmpty = new BigDecimal(params.getPortfolioSize() - portfolio.openPositionCount(rebalanceDate));
        BigDecimal availableCash = portfolio.getCash(rebalanceDate).
                subtract(portfolio.getTransactionCost().
                        multiply(numEmpty));
        for (String fund : selection) {
            if (!portfolio.contains(fund, rebalanceDate)) {
                BigDecimal allocation = availableCash
                        .divide(numEmpty, MathContext.DECIMAL32);
                try {
                    executor.buy(portfolio, fund, rebalanceDate, allocation);
                } catch (PortfolioException e) {
                    e.printStackTrace();
                    throw new StrategyException("Unable to buy winners " + selection +
                            " on " + rebalanceDate +
                            ", current portfolio: " + portfolio.getActiveFunds(rebalanceDate) +
                            ", value: " + portfolio.marketValue(rebalanceDate), e);
                }
            }
        }
    }

    private List<LocalDate> getRebalanceDates(LocalDate fromDate, LocalDate toDate, StrategyServlet.Strategy2Params params) {
        return TradingDayUtils.getWeeklySeries(fromDate, toDate, params.getRebalanceFrequency(), true);
    }

}
