package com.mns.mojoinvest.server.engine.strategy;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioException;
import com.mns.mojoinvest.server.engine.portfolio.SimplePortfolio;
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import com.mns.mojoinvest.shared.params.BacktestParams;
import com.mns.mojoinvest.shared.params.Strategy2Params;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Years;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.logging.Logger;

public class MomentumStrategy2 {

    private static final Logger log = Logger.getLogger(MomentumStrategy2.class.getName());

    private final RelativeStrengthCalculator relativeStrengthCalculator;
    private final Executor executor;
    private final QuoteDao quoteDao;


    Map<String, BigDecimal> initCompares = new HashMap<String, BigDecimal>();
    Map<String, BigDecimal> portfolioCompares = new HashMap<String, BigDecimal>();


    @Inject
    public MomentumStrategy2(RelativeStrengthCalculator relativeStrengthCalculator,
                             Executor executor, QuoteDao dao) {
        this.relativeStrengthCalculator = relativeStrengthCalculator;
        this.executor = executor;
        this.quoteDao = dao;
    }

    public void execute(Portfolio portfolio, BacktestParams backtestParams,
                        Collection<Fund> universe, Strategy2Params params)
            throws StrategyException {

        LocalDate fromDate = new LocalDate(backtestParams.getFromDate());
        LocalDate toDate = new LocalDate(backtestParams.getToDate());

        if (fromDate.isAfter(toDate))
            throw new StrategyException("From date cannot be after to date");

        List<LocalDate> rebalanceDates = getRebalanceDates(fromDate, toDate, params);
        List<Map<String, BigDecimal>> strengths = getRelativeStrengths(universe, params, rebalanceDates);

        DescriptiveStatistics equityCurve = new DescriptiveStatistics(params.getEquityCurveWindow());

        Portfolio shadowPortfolio = new SimplePortfolio(portfolio);

        CSVWriter writer = initialiseCsv();
        writeCsvHeader(universe, writer);

        List<DrawDown> drawDowns = new ArrayList<DrawDown>();
        DrawDown currentDD = null;

        boolean belowEquityCurve = false;

        for (int i = 0; i < rebalanceDates.size(); i++) {

            LocalDate rebalanceDate = rebalanceDates.get(i);
            Map<String, BigDecimal> rs = strengths.get(i);


            if (rs.size() < params.getCastOff()) {
                log.warning(rebalanceDate + " Not enough funds in universe to make selection");
                continue;
            }


            //Shadow portfolio and equity curve calculation stuff
            BigDecimal marketValue = portfolio.marketValue(rebalanceDate);
            BigDecimal shadowMarketValue = shadowPortfolio.marketValue(rebalanceDate);
            equityCurve.addValue(shadowMarketValue.doubleValue());
            BigDecimal equityCurveMA = null;
            if (equityCurve.getN() >= params.getEquityCurveWindow()) {
                equityCurveMA = new BigDecimal(equityCurve.getMean(), MathContext.DECIMAL32);
            }

            logPortfolio(portfolio, rebalanceDate, marketValue, shadowMarketValue, equityCurveMA);

            currentDD = calculateDrawDowns(drawDowns, currentDD, rebalanceDate, marketValue);

            initialiseComparisonsForCsv(universe, rebalanceDate, marketValue);
            String[] compares = calculatePercentChangeForCsv(universe, rebalanceDate);
            writeCsvRow(portfolio, shadowPortfolio, writer, rebalanceDate, marketValue, shadowMarketValue, equityCurveMA, compares);


            List<String> selection = getSelection(rs, params, rebalanceDate);
            if (params.tradeEquityCurve()) {
                sellLosers(shadowPortfolio, rebalanceDate, selection);
                buyWinners(shadowPortfolio, params, rebalanceDate, selection);

                if (equityCurveMA != null && shadowMarketValue.compareTo(equityCurveMA) < 0) {
                    if (!belowEquityCurve) {
                        log.fine("Below equity curve");
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
                        log.fine("Above equity curve");
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


        flushCsvWriter(writer);

        logParams(params);
        logTrades(portfolio);
        logDrawDowns(drawDowns);
        logCAGR(portfolio, fromDate, toDate);

    }


    private DrawDown calculateDrawDowns(List<DrawDown> drawDowns, DrawDown currentDD, LocalDate rebalanceDate, BigDecimal marketValue) {
        //Calculation of draw downs
        if (currentDD == null) {
            currentDD = new DrawDown(rebalanceDate, marketValue);
        }
        //Curve is going up, and min has not been set
        if (marketValue.compareTo(currentDD.getMax()) > 0 &&
                currentDD.getMin() == null) {
            currentDD.setMaxDate(rebalanceDate);
            currentDD.setMax(marketValue);
        }
        //Curve is going down
        else if (marketValue.compareTo(currentDD.getMax()) < 0) {
            //Min has not been set
            if (currentDD.getMin() == null) {
                currentDD.setMinDate(rebalanceDate);
                currentDD.setMin(marketValue);
            }
            //New value is lower than min stored currently
            else if (marketValue.compareTo(currentDD.getMin()) < 0) {
                currentDD.setMinDate(rebalanceDate);
                currentDD.setMin(marketValue);
            }
        }
        //New value is higher than current max - create new drawdown
        else if (marketValue.compareTo(currentDD.getMax()) > 0) {
            drawDowns.add(currentDD);
            currentDD = new DrawDown(rebalanceDate, marketValue);
        }
        return currentDD;
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

    private void logPortfolio(Portfolio portfolio, LocalDate rebalanceDate, BigDecimal marketValue, BigDecimal shadowMarketValue, BigDecimal equityCurveMA) {
        log.info(rebalanceDate + " " +
                portfolio.getActiveFunds(rebalanceDate) + " " +
                marketValue + " " +
                shadowMarketValue + " " + equityCurveMA);
    }

    private void logParams(Strategy2Params strategyParams) {
        log.info("Params: " + strategyParams);
    }

    private void logDrawDowns(List<DrawDown> drawDowns) {
        BigDecimal maxDD = BigDecimal.ZERO;
        for (DrawDown drawDown : drawDowns) {
            if (drawDown.getPctValue().compareTo(maxDD) > 0) {
                maxDD = drawDown.getPctValue();
            }
        }
        log.info("MaxDD: " + maxDD + "%");
    }

    private void logCAGR(Portfolio portfolio, LocalDate fromDate, LocalDate toDate) {
        BigDecimal marketValue = portfolio.marketValue(toDate);
        log.info("Final portfolio value: " + marketValue);
        double base = marketValue.divide(new BigDecimal(portfolio.getParams().getInitialInvestment())).doubleValue();
        double e = 1d / Years.yearsBetween(fromDate, toDate).getYears();
        double cagr = (1 - Math.pow(base, e)) * -100;
        log.info("CAGR: " + cagr + "%");

    }

    private void logTrades(Portfolio portfolio) {
        for (Transaction transaction : portfolio.getTransactions()) {

            log.fine(transaction + "");
        }
        log.info("Number of trades: " + portfolio.getTransactions().size());
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

    private static BigDecimal percentageChange(BigDecimal from, BigDecimal to) {
        BigDecimal change = to.subtract(from);
        return change.divide(from, MathContext.DECIMAL32);
    }


    /*
     * Csv writing stuff - non production
    */

    private CSVWriter initialiseCsv() throws StrategyException {
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter("data/strategy_runs/" + new LocalTime() + ".csv"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new StrategyException("", e);
        }
        return writer;
    }


    private void writeCsvHeader(Collection<Fund> universe, CSVWriter writer) {
        String[] headCompare = new String[universe.size()];
        int k = 0;
        for (Fund fund : universe) {
            headCompare[k] = fund.getSymbol();
            k++;
        }
        String[] headStrat = new String[]{"Date", "Portfolio", "Portfolio Value", "Shadow", "Shadow Value", "Equity Curve"};
        writer.writeNext((String[]) ArrayUtils.addAll(headStrat, headCompare));
    }

    private void initialiseComparisonsForCsv(Collection<Fund> universe, LocalDate rebalanceDate, BigDecimal marketValue) {
        //Initialisation of comparison to portfolio results
        for (Fund fund : universe) {
            if (!initCompares.containsKey(fund.getSymbol())) {
                Quote q = quoteDao.get(fund.getSymbol(), rebalanceDate);
                if (q != null) {
                    initCompares.put(fund.getSymbol(), q.getAdjClose());
                    portfolioCompares.put(fund.getSymbol(), marketValue);
                }
            }
        }
    }

    private String[] calculatePercentChangeForCsv(Collection<Fund> universe, LocalDate rebalanceDate) {
        //Calculate % change for all the funds in universe for later comparison
        String[] compares = new String[universe.size()];
        int p = 0;
        for (Fund fund : universe) {
            if (initCompares.containsKey(fund.getSymbol())) {
                double pct = (percentageChange(initCompares.get(fund.getSymbol()),
                        quoteDao.get(fund.getSymbol(), rebalanceDate).getAdjClose()).doubleValue() + 1)
                        * portfolioCompares.get(fund.getSymbol()).doubleValue();
                compares[p] = pct + "";
            } else {
                compares[p] = "";
            }
            p++;
        }
        return compares;
    }

    private void writeCsvRow(Portfolio portfolio, Portfolio shadowPortfolio, CSVWriter writer, LocalDate rebalanceDate, BigDecimal marketValue, BigDecimal shadowMarketValue, BigDecimal equityCurveMA, String[] compares) {
        String[] bodyStrat = new String[]{rebalanceDate + "",
                portfolio.getActiveFunds(rebalanceDate) + "",
                marketValue + " ",
                shadowPortfolio.getActiveFunds(rebalanceDate) + "",
                shadowMarketValue + " ",
                equityCurveMA == null ? "" : equityCurveMA + ""
        };

        writer.writeNext((String[]) ArrayUtils.addAll(bodyStrat, compares));
    }

    private void flushCsvWriter(CSVWriter writer) {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
