package com.mns.mojoinvest.server.engine.strategy;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.portfolio.Lot;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioException;
import com.mns.mojoinvest.server.engine.portfolio.Position;
import com.mns.mojoinvest.server.servlet.StrategyServlet;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import com.mns.mojoinvest.shared.params.BacktestParams;
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

    public void execute(Portfolio portfolio, BacktestParams backtestParams,
                        Collection<Fund> universe, StrategyServlet.Strategy2Params strategyParams)
            throws StrategyException {

        LocalDate fromDate = new LocalDate(backtestParams.getFromDate());
        LocalDate toDate = new LocalDate(backtestParams.getToDate());

        if (fromDate.isAfter(toDate))
            throw new StrategyException("From date cannot be after to date");

        List<LocalDate> rebalanceDates = getRebalanceDates(fromDate, toDate, strategyParams);

        List<Map<String, BigDecimal>> strengths = getRelativeStrengths(universe, strategyParams, rebalanceDates);

        //TODO:
        //5. Fix equity curve calc
        //5. Fix CAGR calculation
        //6. Add maximum drawdown value

        Portfolio realPortfolio = portfolio;
        boolean belowEquityCurve = false;

        DescriptiveStatistics equityCurve = new DescriptiveStatistics(strategyParams.getEquityCurveWindow());

        for (int i = 0; i < rebalanceDates.size(); i++) {

            LocalDate rebalanceDate = rebalanceDates.get(i);
            Map<String, BigDecimal> rs = strengths.get(i);

            if (rs.size() < strategyParams.getCastOff()) {
                log.warning(rebalanceDate + " Not enough funds in universe to make selection");
                continue;
            }

            BigDecimal marketValue = portfolio.marketValue(rebalanceDate);

            equityCurve.addValue(marketValue.doubleValue());
            BigDecimal equityCurveMA = null;
            if (equityCurve.getN() >= strategyParams.getEquityCurveWindow()) {
                equityCurveMA = new BigDecimal(equityCurve.getMean(), MathContext.DECIMAL32);
            }

            log.info(rebalanceDate + " " + portfolio.getActiveFunds(rebalanceDate) + " " +
                    portfolio.marketValue(rebalanceDate) + " " + realPortfolio.marketValue(rebalanceDate)
                    + " " + equityCurveMA);

            if (strategyParams.isEquityCurveTrading() && equityCurveMA != null) {
                if (!belowEquityCurve && marketValue.compareTo(equityCurveMA) < 0) {
                    log.info("Below equity curve");
                    portfolio = portfolio.createShadow();
                    for (String symbol : realPortfolio.getActiveFunds(rebalanceDate)) {
                        try {
                            executor.sellAll(realPortfolio, symbol, rebalanceDate);
                        } catch (PortfolioException e) {
                            throw new StrategyException("Unable to sell funds when portfolio value " +
                                    "moved under equity curve", e);
                        }
                    }
                    belowEquityCurve = true;
                } else if (belowEquityCurve && marketValue.compareTo(equityCurveMA) > 0) {
                    log.info("Above equity curve");
                    portfolio = realPortfolio;
                    belowEquityCurve = false;
                }

            }

            List<String> selection = getSelection(rs, strategyParams);

            sellLosers(portfolio, rebalanceDate, selection);
            buyWinners(portfolio, strategyParams, rebalanceDate, selection);


        }

        logParams(strategyParams);
        logNumTrades(realPortfolio);
        logCAGR(realPortfolio, rebalanceDates.get(rebalanceDates.size() - 1));

    }

    private List<Map<String, BigDecimal>> getRelativeStrengths(Collection<Fund> universe, StrategyServlet.Strategy2Params strategyParams, List<LocalDate> rebalanceDates)
            throws StrategyException {
        if ("MA".equals(strategyParams.getRelativeStrengthStyle())) {
            return relativeStrengthCalculator
                    .getRelativeStrengthsMA(universe, strategyParams, rebalanceDates);
        } else if ("ROC".equals(strategyParams.getRelativeStrengthStyle())) {
            return relativeStrengthCalculator
                    .getRelativeStrengthsROC(universe, strategyParams, rebalanceDates);
        } else {
            throw new StrategyException("Relative strength style " + strategyParams);
        }
    }

    private void logParams(StrategyServlet.Strategy2Params strategyParams) {
        log.info("Params: " + strategyParams);
    }

    private void logCAGR(Portfolio portfolio, LocalDate date) {
        //                                (1 / # of years)

        BigDecimal marketValue = portfolio.marketValue(date);
        log.info("Final portfolio value: " + marketValue);
        double CAGR = Math.pow(marketValue.divide(new BigDecimal("10000"), MathContext.DECIMAL32).doubleValue(),
                1.0d / 12.0d);
        log.info("CAGR: " + CAGR + "%");

    }

    private void logNumTrades(Portfolio portfolio) {
        int numTrades = 0;
        for (Position position : portfolio.getPositions()) {

            for (Lot lot : position.getLots()) {
                numTrades++; //buy transaction
                numTrades += lot.getSellTransactions().size();
            }
        }
        log.info("Number of trades: " + numTrades);
    }

    private List<String> getSelection(Map<String, BigDecimal> rs, StrategyServlet.Strategy2Params params) {
        //Rank order
        Ordering<String> valueComparator = Ordering.natural()
                .reverse()
                .onResultOf(Functions.forMap(rs))
                .compound(Ordering.natural());
        SortedMap<String, BigDecimal> sorted = ImmutableSortedMap.copyOf(rs, valueComparator);
        log.fine("RS map: " + sorted);
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

    private void buyWinners(Portfolio portfolio, StrategyServlet.Strategy2Params params,
                            LocalDate rebalanceDate, List<String> selection)
            throws StrategyException {

        int numEmpty = params.getPortfolioSize() - portfolio.openPositionCount(rebalanceDate);
        BigDecimal availableCash = portfolio.getCash(rebalanceDate).
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

    private List<LocalDate> getRebalanceDates(LocalDate fromDate, LocalDate toDate, StrategyServlet.Strategy2Params params) {
        return TradingDayUtils.getWeeklySeries(fromDate, toDate, params.getRebalanceFrequency(), true);
    }

}
