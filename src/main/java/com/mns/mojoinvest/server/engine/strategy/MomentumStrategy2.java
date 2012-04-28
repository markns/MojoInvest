package com.mns.mojoinvest.server.engine.strategy;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioException;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import com.mns.mojoinvest.shared.params.BacktestParams;
import com.mns.mojoinvest.shared.params.Strategy2Params;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.logging.Logger;

public class MomentumStrategy2 {

    private static final Logger log = Logger.getLogger(MomentumStrategy2.class.getName());

    private final RelativeStrengthCalculator relativeStrengthCalculator;
    private final Executor executor;

    @Inject
    public MomentumStrategy2(RelativeStrengthCalculator relativeStrengthCalculator,
                             Executor executor) {
        this.relativeStrengthCalculator = relativeStrengthCalculator;
        this.executor = executor;
    }

    public void execute(Portfolio portfolio, Portfolio shadowPortfolio, BacktestParams backtestParams,
                        Collection<Fund> universe, Strategy2Params strategyParams)
            throws StrategyException {

        List<LocalDate> rebalanceDates = getRebalanceDates(backtestParams, strategyParams);

        SortedMap<LocalDate, Map<String, BigDecimal>> relativeStrengthsMap =
                getRelativeStrengths(universe, strategyParams, rebalanceDates);

        assert rebalanceDates.size() == relativeStrengthsMap.size() : "number of rebalance dates didn't match " +
                "number of relative strength dates received";

        DescriptiveStatistics equityCurve = new DescriptiveStatistics(strategyParams.getEquityCurveWindow());
        boolean belowEquityCurve = false;

        for (LocalDate date : relativeStrengthsMap.keySet()) {

            Map<String, BigDecimal> strengths = relativeStrengthsMap.get(date);

            if (strengths.size() < strategyParams.getCastOff()) {
                log.warning(date + " Not enough funds in universe to make selection");
                continue;
            }

            //Shadow portfolio and equity curve calculation stuff
            equityCurve.addValue(shadowPortfolio.marketValue(date).doubleValue());
            BigDecimal equityCurveMA = null;
            if (equityCurve.getN() >= strategyParams.getEquityCurveWindow()) {
                equityCurveMA = new BigDecimal(equityCurve.getMean(), MathContext.DECIMAL32);
            }

            log.info(date + " " +
                    portfolio.getActiveFunds(date) + " " +
                    portfolio.marketValue(date) + " " +
                    shadowPortfolio.marketValue(date) + " " +
                    equityCurveMA);

            List<String> selection = getSelection(date, strategyParams, strengths);

            if (strategyParams.tradeEquityCurve()) {

                rebalance(date, strategyParams, shadowPortfolio, selection);

                if (equityCurveMA != null && shadowPortfolio.marketValue(date).compareTo(equityCurveMA) < 0) {
                    if (!belowEquityCurve) {
                        log.fine("Crossed below equity curve");
                        belowEquityCurve = true;
                        for (String symbol : portfolio.getActiveFunds(date)) {
                            try {
                                executor.sellAll(portfolio, symbol, date);
                            } catch (PortfolioException e) {
                                throw new StrategyException("Unable to sell funds when portfolio value " +
                                        "moved under equity curve", e);
                            }
                        }
                        if (strategyParams.useSafeAsset()) {
                            int numEmpty = strategyParams.getPortfolioSize() - portfolio.openPositionCount(
                                    TradingDayUtils.rollForward(date.plusDays(1)));
                            BigDecimal availableCash = portfolio.getCash(
                                    TradingDayUtils.rollForward(date.plusDays(1))).
                                    subtract(portfolio.getTransactionCost().
                                            multiply(new BigDecimal(numEmpty)));
                            try {
                                executor.buy(portfolio, strategyParams.getSafeAsset(),
                                        date, availableCash);
                            } catch (PortfolioException e) {
                                log.warning(date + " Unable to move into safe asset");
                            }
                        }
                    }
                } else {
                    if (belowEquityCurve) {
                        log.fine("Crossed above equity curve");
                        if (strategyParams.useSafeAsset() &&
                                portfolio.contains(strategyParams.getSafeAsset(), date)) {
                            try {
                                executor.sellAll(portfolio, strategyParams.getSafeAsset(), date);
                            } catch (PortfolioException e) {
                                throw new StrategyException(date + " Unable to move out of safe asset", e);
                            }
                        }
                        belowEquityCurve = false;
                    }
                    rebalance(date, strategyParams, portfolio, selection);
                }
            } else {
                rebalance(date, strategyParams, portfolio, selection);
            }
        }


    }

    private void rebalance(LocalDate rebalanceDate, Strategy2Params params, Portfolio portfolio,
                           List<String> selection) throws StrategyException {
        sellLosers(portfolio, rebalanceDate, selection);
        buyWinners(portfolio, params, rebalanceDate, selection);
    }


    private SortedMap<LocalDate, Map<String, BigDecimal>> getRelativeStrengths(Collection<Fund> universe,
                                                                               Strategy2Params strategyParams,
                                                                               List<LocalDate> rebalanceDates)
            throws StrategyException {
        if ("MA".equals(strategyParams.getRelativeStrengthStyle())) {
            return relativeStrengthCalculator
                    .getRelativeStrengthsMA(universe, strategyParams, rebalanceDates);
        } else if ("ROC".equals(strategyParams.getRelativeStrengthStyle())) {
            return relativeStrengthCalculator
                    .getRelativeStrengthsROC(universe, strategyParams, rebalanceDates);
        } else if ("ALPHA".equals(strategyParams.getRelativeStrengthStyle())) {
            return relativeStrengthCalculator
                    .getRelativeStrengthAlpha(universe, strategyParams, rebalanceDates);
        } else {
            throw new StrategyException("Relative strength style " + strategyParams);
        }
    }


    private List<String> getSelection(LocalDate date, Strategy2Params params, Map<String, BigDecimal> rs) {
        //Rank order
        Ordering<String> valueComparator = Ordering.natural()
                .reverse()
                .onResultOf(Functions.forMap(rs))
                .compound(Ordering.natural());
        SortedMap<String, BigDecimal> sorted = ImmutableSortedMap.copyOf(rs, valueComparator);
        log.fine(date + " RS(" + params.getRelativeStrengthStyle() + "): " + sorted);
        List<String> rank = new ArrayList<String>(sorted.keySet());
        return rank.subList(0, params.getCastOff());
    }

    private void sellLosers(Portfolio portfolio, LocalDate rebalanceDate, List<String> selection)
            throws StrategyException {

        for (String symbol : portfolio.getActiveFunds(rebalanceDate)) {
            if (!selection.contains(symbol)) {
                try {
                    executor.sellAll(portfolio, symbol, rebalanceDate);
                } catch (PortfolioException e) {
                    throw new StrategyException("Unable to sell losers " + selection +
                            " on " + rebalanceDate +
                            ", current portfolio: " + portfolio.getActiveFunds(rebalanceDate) +
                            ", value: " + portfolio.marketValue(rebalanceDate), e);
                }
            }
        }
    }

    private void buyWinners(Portfolio portfolio, Strategy2Params params,
                            LocalDate rebalanceDate, List<String> selection)
            throws StrategyException {

        int numEmpty = params.getPortfolioSize() - portfolio.openPositionCount(
                TradingDayUtils.rollForward(rebalanceDate.plusDays(1)));
        BigDecimal availableCash = portfolio.getCash(
                TradingDayUtils.rollForward(rebalanceDate.plusDays(1))).
                subtract(portfolio.getTransactionCost().
                        multiply(new BigDecimal(numEmpty)));

        int added = 0;
        for (String symbol : selection) {
            if (numEmpty == added)
                break;
            if (!portfolio.contains(symbol, rebalanceDate)) {
                try {
                    BigDecimal allocation = availableCash
                            .divide(new BigDecimal(numEmpty), MathContext.DECIMAL32);
                    executor.buy(portfolio, symbol, rebalanceDate, allocation);
                    added++;
                } catch (PortfolioException e) {
                    throw new StrategyException("Unable to buy winners " + selection +
                            " on " + rebalanceDate +
                            ", current portfolio: " + portfolio.getActiveFunds(rebalanceDate) +
                            ", value: " + portfolio.marketValue(rebalanceDate), e);
                }
            }
        }
    }

    private List<LocalDate> getRebalanceDates(BacktestParams backtestParams, Strategy2Params params)
            throws StrategyException {
        LocalDate fromDate = new LocalDate(backtestParams.getFromDate());
        LocalDate toDate = new LocalDate(backtestParams.getToDate());

        if (fromDate.isAfter(toDate))
            throw new StrategyException("From date cannot be after to date");

        return TradingDayUtils.getEndOfWeekSeries(fromDate, toDate, params.getRebalanceFrequency());
    }


}
