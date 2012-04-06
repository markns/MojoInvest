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
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import com.mns.mojoinvest.server.servlet.StrategyServlet;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import com.mns.mojoinvest.shared.params.BacktestParams;
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

    @Inject
    public MomentumStrategy2(RelativeStrengthCalculator relativeStrengthCalculator,
                             Executor executor, QuoteDao dao) {
        this.relativeStrengthCalculator = relativeStrengthCalculator;
        this.executor = executor;
        this.quoteDao = dao;
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

        DescriptiveStatistics equityCurve = new DescriptiveStatistics(strategyParams.getEquityCurveWindow());

        Portfolio shadowPortfolio = portfolio.createShadow();

        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter("data/strategy_runs/" + new LocalTime() + ".csv"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new StrategyException("", e);
        }

        String[] headCompare = new String[universe.size()];
        int k = 0;
        for (Fund fund : universe) {
            headCompare[k] = fund.getSymbol();
            k++;
        }
        String[] headStrat = new String[]{"Date", "Portfolio", "Shadow", "Equity Curve"};
        writer.writeNext((String[]) ArrayUtils.addAll(headStrat, headCompare));

        Map<String, BigDecimal> initCompares = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> portfolioCompares = new HashMap<String, BigDecimal>();


        for (int i = 0; i < rebalanceDates.size(); i++) {

            LocalDate rebalanceDate = rebalanceDates.get(i);
            Map<String, BigDecimal> rs = strengths.get(i);


            if (rs.size() < strategyParams.getCastOff()) {
                log.warning(rebalanceDate + " Not enough funds in universe to make selection");
                continue;
            }

            //for each fund in universe
            //check if an initial quote exists already
            //if not, try to retrieve
            //if not null store in initCompares
            for (Fund fund : universe) {
                if (!initCompares.containsKey(fund.getSymbol())) {
                    Quote q = quoteDao.get(fund.getSymbol(), rebalanceDate);
                    if (q != null) {
                        initCompares.put(fund.getSymbol(), q.getAdjClose());
                        portfolioCompares.put(fund.getSymbol(), portfolio.marketValue(rebalanceDate));
                    }
                }
            }


            BigDecimal shadowPortfolioValue = shadowPortfolio.marketValue(rebalanceDate);

            equityCurve.addValue(shadowPortfolioValue.doubleValue());

            BigDecimal equityCurveMA = null;

            if (equityCurve.getN() >= strategyParams.getEquityCurveWindow()) {
                equityCurveMA = new BigDecimal(equityCurve.getMean(), MathContext.DECIMAL32);
            }

            log.info(rebalanceDate.dayOfWeek().getAsShortText() + " " + rebalanceDate + " " +
                    portfolio.getActiveFunds(rebalanceDate) + " " +
                    portfolio.marketValue(rebalanceDate) + " " +
                    shadowPortfolio.marketValue(rebalanceDate) + " " + equityCurveMA);

            int p = 0;
            String[] compares = new String[universe.size()];
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

            String[] bodyStrat = new String[]{rebalanceDate + "",
                    portfolio.marketValue(rebalanceDate) + " ",
                    shadowPortfolio.marketValue(rebalanceDate) + " ",
                    equityCurveMA == null ? "" : equityCurveMA + ""
            };

            writer.writeNext((String[]) ArrayUtils.addAll(bodyStrat, compares));

            if (strategyParams.tradeEquityCurve()) {
                if (equityCurveMA != null && shadowPortfolioValue.compareTo(equityCurveMA) < 0) {
                    log.fine("Below equity curve");

                    for (String symbol : portfolio.getActiveFunds(rebalanceDate)) {
                        try {
                            executor.sellAll(portfolio, symbol, rebalanceDate);
                        } catch (PortfolioException e) {
                            throw new StrategyException("Unable to sell funds when portfolio value " +
                                    "moved under equity curve", e);
                        }
                    }
                    List<String> selection = getSelection(rs, strategyParams, rebalanceDate);
                    sellLosers(shadowPortfolio, rebalanceDate, selection);
                    buyWinners(shadowPortfolio, strategyParams, rebalanceDate, selection);
                } else {

                    List<String> selection = getSelection(rs, strategyParams, rebalanceDate);
                    sellLosers(shadowPortfolio, rebalanceDate, selection);
                    buyWinners(shadowPortfolio, strategyParams, rebalanceDate, selection);
                    sellLosers(portfolio, rebalanceDate, selection);
                    buyWinners(portfolio, strategyParams, rebalanceDate, selection);
                }
            } else {

                List<String> selection = getSelection(rs, strategyParams, rebalanceDate);
                sellLosers(portfolio, rebalanceDate, selection);
                buyWinners(portfolio, strategyParams, rebalanceDate, selection);
            }
        }


        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO:
        //Add maximum drawdown value

        logParams(strategyParams);
        logTrades(portfolio);
        logCAGR(portfolio, fromDate, toDate);

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

    private List<String> getSelection(Map<String, BigDecimal> rs, StrategyServlet.Strategy2Params params, LocalDate date) {
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

    private void buyWinners(Portfolio portfolio, StrategyServlet.Strategy2Params params,
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

    private List<LocalDate> getRebalanceDates(LocalDate fromDate, LocalDate toDate, StrategyServlet.Strategy2Params params) {
        return TradingDayUtils.getEndOfWeekSeries(fromDate, toDate, params.getRebalanceFrequency());
    }


    private static BigDecimal percentageChange(BigDecimal from, BigDecimal to) {
        BigDecimal change = to.subtract(from);
        return change.divide(from, MathContext.DECIMAL32);
    }
}
