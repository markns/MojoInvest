package com.mns.mojoinvest.server.engine.strategy;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.calculator.RelativeStrengthCalculator;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.params.Params;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioException;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioFactory;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.logging.Logger;

public class MomentumStrategy {

    private static final Logger log = Logger.getLogger(MomentumStrategy.class.getName());

    private final RelativeStrengthCalculator relativeStrengthCalculator;
    private final Executor executor;
    private final PortfolioFactory portfolioFactory;

    public static final String SHADOW_EQUITY_CURVE = "Shadow Equity Curve";
    public static final String SHADOW_PORTFOLIO_MARKET_VALUE = "Shadow Portfolio Market Value";

    @Inject
    public MomentumStrategy(RelativeStrengthCalculator relativeStrengthCalculator,
                            PortfolioFactory portfolioFactory, Executor executor) {
        this.relativeStrengthCalculator = relativeStrengthCalculator;
        this.portfolioFactory = portfolioFactory;
        this.executor = executor;
    }

    public Map<String, Map<LocalDate, BigDecimal>> execute(Portfolio portfolio, Params params,
                                                           Collection<Fund> universe)
            throws StrategyException {

        List<LocalDate> rebalanceDates = getRebalanceDates(params);

        SortedMap<LocalDate, Map<String, BigDecimal>> relativeStrengthsMap =
                getRelativeStrengths(universe, params, rebalanceDates);

        if (params.isRiskAdjusted()) {
            relativeStrengthsMap = relativeStrengthCalculator.adjustRelativeStrengths(relativeStrengthsMap, universe,
                    params, rebalanceDates);
        }

        Map<String, Map<LocalDate, BigDecimal>> additionalResults = new HashMap<String, Map<LocalDate, BigDecimal>>();

        log.fine("Running strategy");
        long start = System.currentTimeMillis();
        if (params.isTradeEquityCurve()) {
            runStrategyWithEquityCurve(portfolio, params, rebalanceDates, relativeStrengthsMap, additionalResults);
        } else {
            runStrategy(portfolio, params, rebalanceDates, relativeStrengthsMap);
        }
        log.fine("Rebalancing took " + (System.currentTimeMillis() - start) + " ms");
        return additionalResults;
    }

    private void runStrategy(Portfolio portfolio, Params params, List<LocalDate> rebalanceDates,
                             SortedMap<LocalDate, Map<String, BigDecimal>> relativeStrengthsMap) throws StrategyException {
        for (LocalDate date : rebalanceDates) {
            log.fine("** " + date + " **");
            Map<String, BigDecimal> strengths = relativeStrengthsMap.get(date);
            if (strengths.size() < params.getCastOff()) {
                log.info(date + " Not enough funds in universe to make selection");
                continue;
            }
            List<String> selection = getSelection(date, params, strengths);
            rebalance(portfolio, date, selection, params);
        }
    }

    private Map<String, Map<LocalDate, BigDecimal>> runStrategyWithEquityCurve(Portfolio portfolio, Params params, List<LocalDate> rebalanceDates,
                                                                               SortedMap<LocalDate, Map<String, BigDecimal>> relativeStrengthsMap,
                                                                               Map<String, Map<LocalDate, BigDecimal>> additionalResults)
            throws StrategyException {

        Portfolio shadowPortfolio = portfolioFactory.create(params, true);

        DescriptiveStatistics shadowEquityCurve = new DescriptiveStatistics(params.getEquityCurveWindow());
        boolean belowEquityCurve = false;

        additionalResults.put(SHADOW_EQUITY_CURVE, new HashMap<LocalDate, BigDecimal>(relativeStrengthsMap.size()));
        additionalResults.put(SHADOW_PORTFOLIO_MARKET_VALUE, new HashMap<LocalDate, BigDecimal>(relativeStrengthsMap.size()));

        for (LocalDate date : rebalanceDates) {
            log.fine("** " + date + " ** - Open positions: " + portfolio.getActiveSymbols(date));

            Map<String, BigDecimal> strengths = relativeStrengthsMap.get(date);

            if (strengths.size() < params.getCastOff()) {
                log.info(date + " Not enough funds in universe to make selection");
                continue;
            }

            List<String> selection = getSelection(date, params, strengths);

            //Shadow portfolio and equity curve calculation stuff
            BigDecimal shadowMarketValue = null;
            try {
                shadowMarketValue = shadowPortfolio.marketValue(date);
            } catch (PortfolioException e) {
                throw new StrategyException("Unable to complete strategy run at " + date, e);
            }
            shadowEquityCurve.addValue(shadowMarketValue.doubleValue());
            BigDecimal equityCurveMA = null;
            if (shadowEquityCurve.getN() >= params.getEquityCurveWindow()) {
                equityCurveMA = new BigDecimal(shadowEquityCurve.getMean(), MathContext.DECIMAL32);
            }
            additionalResults.get(SHADOW_PORTFOLIO_MARKET_VALUE).put(date, shadowMarketValue);
            additionalResults.get(SHADOW_EQUITY_CURVE).put(date, equityCurveMA);

            rebalance(shadowPortfolio, date, selection, params);
            if (equityCurveMA != null && shadowMarketValue.compareTo(equityCurveMA) < 0) {
                if (!belowEquityCurve) {
                    log.fine(date + " Crossed below equity curve");
                    belowEquityCurve = true;
                    sellEverything(portfolio, date, params);
                    if (params.isUseSafeAsset()) {
                        buySafeAsset(portfolio, params, date);
                    }
                }
            } else {
                if (belowEquityCurve) {
                    log.fine(date + " Crossed above equity curve");
                    sellSafeAsset(portfolio, params, date);
                    belowEquityCurve = false;
                }
                rebalance(portfolio, date, selection, params);
            }

        }
        return additionalResults;
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
//        log.fine(date + " RS(" + params.getRelativeStrengthStyle() + "): " + sorted);
        List<String> rank = new ArrayList<String>(sorted.keySet());
        return rank.subList(0, params.getCastOff());
    }

    private void rebalance(Portfolio portfolio, LocalDate rebalanceDate, List<String> selection, Params params) throws StrategyException {
        sellLosers(portfolio, rebalanceDate, selection, params);
        buyWinners(portfolio, params, rebalanceDate, selection);
    }


    private void sellLosers(Portfolio portfolio, LocalDate rebalanceDate, List<String> selection, Params params)
            throws StrategyException {

        for (String symbol : portfolio.getActiveSymbols(rebalanceDate)) {
            if (!selection.contains(symbol)) {
                try {
                    if (portfolio.getPosition(symbol).canSellOn(rebalanceDate, params.getMinHoldingPeriod())) {
                        executor.sellAll(portfolio, symbol, rebalanceDate);
                    } else {
                        log.fine(rebalanceDate + " Unable to sell " + symbol + " due to minimum holding period");
                    }
                } catch (PortfolioException e) {
                    throw new StrategyException("Unable to sell losers " + selection +
                            " on " + rebalanceDate +
                            ", current portfolio: " + portfolio.getActiveSymbols(rebalanceDate));
                }
            }
        }
    }


    private void buyWinners(Portfolio portfolio, Params params,
                            LocalDate rebalanceDate, List<String> selection)
            throws StrategyException {

        //Check how many portfolio slots there are on the day after rebalance date.
        //(We've already sold positions, however they won't show as empty until the next day)
        //TODO: Could store a member var detailing how many positions have been sold
        int numEmpty = params.getPortfolioSize() - portfolio.openPositionCount(getExecutionDate(rebalanceDate));
        //Check how many cash we'll have on the day after rebalance date.
        BigDecimal availableCash = portfolio.getCash(getExecutionDate(rebalanceDate))
                .subtract(portfolio.getTransactionCost()
                        .multiply(new BigDecimal(numEmpty)));

        int added = 0;
        for (String symbol : selection) {
            if (numEmpty == added || portfolio.inSafeAsset())
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
                            ", current portfolio: " + portfolio.getActiveSymbols(rebalanceDate) +
                            " - " + e.getMessage());
                }
            }
        }
    }

    private void sellEverything(Portfolio portfolio, LocalDate date, Params params) throws StrategyException {
        for (String symbol : portfolio.getActiveSymbols(date)) {
            try {
                if (portfolio.getPosition(symbol).canSellOn(date, params.getMinHoldingPeriod())) {
                    executor.sellAll(portfolio, symbol, date);
                } else {
                    log.fine(date + " Unable to sell " + symbol + " due to minimum holding period");
                }
            } catch (PortfolioException e) {
                throw new StrategyException("Unable to sell funds when portfolio value " +
                        "moved under equity curve", e);
            }
        }
    }

    private void buySafeAsset(Portfolio portfolio, Params params, LocalDate date) {
        if (params.getPortfolioSize() - portfolio.openPositionCount(getExecutionDate(date)) < 1) {
            log.fine(date + " Unable to buy safe asset due to no empty position slots");
            return;
        }

        BigDecimal availableCash = portfolio.getCash(getExecutionDate(date))
                .subtract(portfolio.getTransactionCost());
        try {
            executor.buy(portfolio, params.getSafeAsset(), date, availableCash);
            portfolio.setInSafeAsset(true);
        } catch (PortfolioException e) {
            log.warning(date + " Unable to move into safe asset: " + e.getMessage());
        }
    }

    private void sellSafeAsset(Portfolio portfolio, Params params, LocalDate date) throws StrategyException {
        if (params.isUseSafeAsset() &&
                portfolio.contains(params.getSafeAsset(), date)) {
            try {
                if (portfolio.getPosition(params.getSafeAsset()).canSellOn(date, params.getMinHoldingPeriod())) {
                    executor.sellAll(portfolio, params.getSafeAsset(), date);
                    portfolio.setInSafeAsset(false);
                } else {
                    log.fine(date + " Unable to sell safe asset " + params.getSafeAsset() + " due to minimum holding period");
                }
            } catch (PortfolioException e) {
                throw new StrategyException(date + " Unable to move out of safe asset", e);
            }
        }
    }

    private LocalDate getExecutionDate(LocalDate date) {
        return TradingDayUtils.rollForward(date.plusDays(1));
    }


}
