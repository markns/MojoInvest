package com.mns.mojoinvest.server.engine.strategy;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.params.Params;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioException;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.logging.Logger;

public class MomentumStrategy {

    private static final Logger log = Logger.getLogger(MomentumStrategy.class.getName());
    public static final String SHADOW_EQUITY_CURVE = "Shadow Equity Curve";

    private final RelativeStrengthCalculator relativeStrengthCalculator;
    private final Executor executor;

    @Inject
    public MomentumStrategy(RelativeStrengthCalculator relativeStrengthCalculator,
                            Executor executor) {
        this.relativeStrengthCalculator = relativeStrengthCalculator;
        this.executor = executor;
    }

    public Map<String, Map<LocalDate, BigDecimal>> execute(Portfolio portfolio, Portfolio shadowPortfolio,
                                                           Params params, Collection<Fund> universe)
            throws StrategyException {

        List<LocalDate> rebalanceDates = getRebalanceDates(params);

        SortedMap<LocalDate, Map<String, BigDecimal>> relativeStrengthsMap =
                getRelativeStrengths(universe, params, rebalanceDates);

        assert rebalanceDates.size() == relativeStrengthsMap.size() : "number of rebalance dates didn't match " +
                "number of relative strength dates received";

        if (params.isRiskAdjust()) {
            relativeStrengthsMap = relativeStrengthCalculator.adjustRelativeStrengths(relativeStrengthsMap, universe,
                    params, rebalanceDates);
        }

        DescriptiveStatistics shadowPortfolioEquityCurve = new DescriptiveStatistics(params.getEquityCurveWindow());
        boolean belowEquityCurve = false;

        Map<String, Map<LocalDate, BigDecimal>> additionalResults = new HashMap<String, Map<LocalDate, BigDecimal>>();
        additionalResults.put(SHADOW_EQUITY_CURVE, new HashMap<LocalDate, BigDecimal>(relativeStrengthsMap.size()));

        for (LocalDate date : relativeStrengthsMap.keySet()) {

            Map<String, BigDecimal> strengths = relativeStrengthsMap.get(date);

            if (strengths.size() < params.getCastOff()) {
                log.warning(date + " Not enough funds in universe to make selection");
                continue;
            }

            List<String> selection = getSelection(date, params, strengths);

            if (params.isEquityCurveTrading()) {
                //Shadow portfolio and equity curve calculation stuff
                shadowPortfolioEquityCurve.addValue(shadowPortfolio.marketValue(date).doubleValue());
                BigDecimal equityCurveMA = null;
                if (shadowPortfolioEquityCurve.getN() >= params.getEquityCurveWindow()) {
                    equityCurveMA = new BigDecimal(shadowPortfolioEquityCurve.getMean(), MathContext.DECIMAL32);
                }
                additionalResults.get(SHADOW_EQUITY_CURVE).put(date, equityCurveMA);

                log.info(date + " " + portfolio.getActiveFunds(date) + " " + portfolio.marketValue(date) + " " +
                        shadowPortfolio.marketValue(date) + " " + equityCurveMA);

                rebalance(shadowPortfolio, date, selection, params);
                if (equityCurveMA != null && shadowPortfolio.marketValue(date).compareTo(equityCurveMA) < 0) {
                    if (!belowEquityCurve) {
                        log.fine("Crossed below equity curve");
                        belowEquityCurve = true;
                        sellEverything(portfolio, date);
                        if (params.isUseSafeAsset()) {
                            buySafeAsset(portfolio, params, date);
                        }
                    }
                } else {
                    if (belowEquityCurve) {
                        log.fine("Crossed above equity curve");
                        sellSafeAsset(portfolio, params, date);
                        belowEquityCurve = false;
                    }
                    rebalance(portfolio, date, selection, params);
                }
            } else {
                log.info(date + " " + portfolio.getActiveFunds(date) + " " + portfolio.marketValue(date));
                rebalance(portfolio, date, selection, params);
            }
        }

        return additionalResults;
    }

    private void sellSafeAsset(Portfolio portfolio, Params params, LocalDate date) throws StrategyException {
        if (params.isUseSafeAsset() &&
                portfolio.contains(params.getSafeAsset(), date)) {
            try {
                executor.sellAll(portfolio, params.getSafeAsset(), date);
            } catch (PortfolioException e) {
                throw new StrategyException(date + " Unable to move out of safe asset", e);
            }
        }
    }

    private void buySafeAsset(Portfolio portfolio, Params params, LocalDate date) {
        int numEmpty = params.getPortfolioSize() - portfolio.openPositionCount(
                TradingDayUtils.rollForward(date.plusDays(1)));
        BigDecimal availableCash = portfolio.getCash(
                TradingDayUtils.rollForward(date.plusDays(1))).
                subtract(portfolio.getTransactionCost().
                        multiply(new BigDecimal(numEmpty)));
        try {
            executor.buy(portfolio, params.getSafeAsset(),
                    date, availableCash);
        } catch (PortfolioException e) {
            log.warning(date + " Unable to move into safe asset");
        }
    }

    private void sellEverything(Portfolio portfolio, LocalDate date) throws StrategyException {
        for (String symbol : portfolio.getActiveFunds(date)) {
            try {
                executor.sellAll(portfolio, symbol, date);
            } catch (PortfolioException e) {
                throw new StrategyException("Unable to sell funds when portfolio value " +
                        "moved under equity curve", e);
            }
        }
    }

    private List<LocalDate> getRebalanceDates(Params params)
            throws StrategyException {
        LocalDate fromDate = params.getFromDate();
        LocalDate toDate = params.getToDate();

        if (fromDate.isAfter(toDate))
            throw new StrategyException("From date cannot be after to date");

        return TradingDayUtils.getEndOfWeekSeries(fromDate, toDate, params.getRebalanceFrequency());
    }


    private SortedMap<LocalDate, Map<String, BigDecimal>> getRelativeStrengths(Collection<Fund> universe,
                                                                               Params params,
                                                                               List<LocalDate> rebalanceDates)
            throws StrategyException {
        if ("MA".equals(params.getRelativeStrengthStyle())) {
            return relativeStrengthCalculator
                    .getRelativeStrengthsMA(universe, params, rebalanceDates);
        } else if ("ROC".equals(params.getRelativeStrengthStyle())) {
            return relativeStrengthCalculator
                    .getRelativeStrengthsROC(universe, params, rebalanceDates);
        } else if ("ALPHA".equals(params.getRelativeStrengthStyle())) {
            return relativeStrengthCalculator
                    .getRelativeStrengthAlpha(universe, params, rebalanceDates);
        } else {
            throw new StrategyException("Relative strength style " + params);
        }
    }


    private List<String> getSelection(LocalDate date, Params params, Map<String, BigDecimal> rs) {
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

    private void rebalance(Portfolio portfolio, LocalDate rebalanceDate, List<String> selection, Params params) throws StrategyException {
        sellLosers(portfolio, rebalanceDate, selection);
        buyWinners(portfolio, params, rebalanceDate, selection);
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

    private void buyWinners(Portfolio portfolio, Params params,
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


}
