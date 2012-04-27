package com.mns.mojoinvest.server.engine.strategy;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
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
    private final QuoteDao quoteDao;


    @Inject
    public MomentumStrategy2(RelativeStrengthCalculator relativeStrengthCalculator,
                             Executor executor, QuoteDao dao) {
        this.relativeStrengthCalculator = relativeStrengthCalculator;
        this.executor = executor;
        this.quoteDao = dao;
    }

    public void execute(Portfolio portfolio, Portfolio shadowPortfolio, BacktestParams backtestParams,
                        Collection<Fund> universe, Strategy2Params params)
            throws StrategyException {

        LocalDate fromDate = new LocalDate(backtestParams.getFromDate());
        LocalDate toDate = new LocalDate(backtestParams.getToDate());

        if (fromDate.isAfter(toDate))
            throw new StrategyException("From date cannot be after to date");

        List<LocalDate> rebalanceDates = getRebalanceDates(fromDate, toDate, params);
        List<Map<String, BigDecimal>> strengths = getRelativeStrengths(universe, params, rebalanceDates);

        DescriptiveStatistics equityCurve = new DescriptiveStatistics(params.getEquityCurveWindow());

        boolean belowEquityCurve = false;

        for (int i = 0; i < rebalanceDates.size(); i++) {

            LocalDate rebalanceDate = rebalanceDates.get(i);
            Map<String, BigDecimal> rs = strengths.get(i);

            if (rs.size() < params.getCastOff()) {
                log.warning(rebalanceDate + " Not enough funds in universe to make selection");
                continue;
            }


            //Shadow portfolio and equity curve calculation stuff
            equityCurve.addValue(shadowPortfolio.marketValue(rebalanceDate).doubleValue());
            BigDecimal equityCurveMA = null;
            if (equityCurve.getN() >= params.getEquityCurveWindow()) {
                equityCurveMA = new BigDecimal(equityCurve.getMean(), MathContext.DECIMAL32);
            }

            log.info(rebalanceDate + " " +
                    portfolio.getActiveFunds(rebalanceDate) + " " +
                    portfolio.marketValue(rebalanceDate) + " " +
                    shadowPortfolio.marketValue(rebalanceDate) + " " + equityCurveMA);

            List<String> selection = getSelection(rs, params, rebalanceDate);
            if (params.tradeEquityCurve()) {
                sellLosers(shadowPortfolio, rebalanceDate, selection);
                buyWinners(shadowPortfolio, params, rebalanceDate, selection);

                if (equityCurveMA != null && shadowPortfolio.marketValue(rebalanceDate).compareTo(equityCurveMA) < 0) {
                    if (!belowEquityCurve) {
                        log.fine("Crossed below equity curve");
                        belowEquityCurve = true;
                        for (String symbol : portfolio.getActiveFunds(rebalanceDate)) {
                            try {
                                executor.sellAll(portfolio, symbol, rebalanceDate);
                            } catch (PortfolioException e) {
                                throw new StrategyException("Unable to sell funds when portfolio value " +
                                        "moved under equity curve", e);
                            }
                        }
                        if (params.useSafeAsset()) {
                            int numEmpty = params.getPortfolioSize() - portfolio.openPositionCount(
                                    TradingDayUtils.rollForward(rebalanceDate.plusDays(1)));
                            BigDecimal availableCash = portfolio.getCash(
                                    TradingDayUtils.rollForward(rebalanceDate.plusDays(1))).
                                    subtract(portfolio.getTransactionCost().
                                            multiply(new BigDecimal(numEmpty)));
                            try {
                                executor.buy(portfolio, params.getSafeAsset(),
                                        rebalanceDate, availableCash);
                            } catch (PortfolioException e) {
                                log.warning(rebalanceDate + " Unable to move into safe asset");
                            }
                        }
                    }
                } else {
                    if (belowEquityCurve) {
                        log.fine("Crossed above equity curve");
                        if (params.useSafeAsset() &&
                                portfolio.contains(params.getSafeAsset(), rebalanceDate)) {
                            try {
                                executor.sellAll(portfolio, params.getSafeAsset(), rebalanceDate);
                            } catch (PortfolioException e) {
                                throw new StrategyException(rebalanceDate + " Unable to move out of safe asset", e);
                            }
                        }
                        belowEquityCurve = false;
                    }
                    sellLosers(portfolio, rebalanceDate, selection);
                    buyWinners(portfolio, params, rebalanceDate, selection);
                }
            } else {
                sellLosers(portfolio, rebalanceDate, selection);
                buyWinners(portfolio, params, rebalanceDate, selection);
            }
        }


    }


    private List<Map<String, BigDecimal>> getRelativeStrengths(Collection<Fund> universe,
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


    private List<String> getSelection(Map<String, BigDecimal> rs, Strategy2Params params, LocalDate date) {
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

    private List<LocalDate> getRebalanceDates(LocalDate fromDate, LocalDate toDate, Strategy2Params params) {
        return TradingDayUtils.getEndOfWeekSeries(fromDate, toDate, params.getRebalanceFrequency());
    }


}
