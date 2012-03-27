package com.mns.mojoinvest.server.engine.strategy;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioException;
import com.mns.mojoinvest.server.servlet.StrategyServlet;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import com.mns.mojoinvest.shared.params.BacktestParams;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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

        log.info("Starting load of calculated values");
        //TODO: Factor calculation of RS to separate class
        Collection<CalculatedValue> ma1s = calculatedValueDao.get(rebalanceDates, universe,
                "SMA", strategyParams.getMa1());
        Collection<CalculatedValue> ma2s = calculatedValueDao.get(rebalanceDates, universe,
                "SMA", strategyParams.getMa2());

        log.info("Building intermediate data structures");
        //CalculatedValue -> Data|Symbol|Type|Period
        Map<LocalDate, Map<String, BigDecimal>> ma1Map = new HashMap<LocalDate, Map<String, BigDecimal>>();
        for (CalculatedValue ma1 : ma1s) {
            if (!ma1Map.containsKey(ma1.getDate())) {
                ma1Map.put(ma1.getDate(), new HashMap<String, BigDecimal>());
            }
            ma1Map.get(ma1.getDate()).put(ma1.getSymbol(), ma1.getValue());
        }

        Map<LocalDate, Map<String, BigDecimal>> ma2Map = new HashMap<LocalDate, Map<String, BigDecimal>>();
        for (CalculatedValue ma2 : ma2s) {
            if (!ma2Map.containsKey(ma2.getDate())) {
                ma2Map.put(ma2.getDate(), new HashMap<String, BigDecimal>());
            }
            ma2Map.get(ma2.getDate()).put(ma2.getSymbol(), ma2.getValue());
        }


        for (LocalDate rebalanceDate : rebalanceDates) {

            //if portfolio.marketValue(rebalanceDate) is below the x period moving average (equity curve)
            //  Portfolio realPortfolio = portfolio
            //  Portfolio portfolio = realPortfolio.clone();


            if (ma1Map.containsKey(rebalanceDate) && ma2Map.containsKey(rebalanceDate)) {
                Map<String, BigDecimal> ma1vals = ma1Map.get(rebalanceDate);
                Map<String, BigDecimal> ma2vals = ma2Map.get(rebalanceDate);
                Map<String, BigDecimal> rs = new HashMap<String, BigDecimal>();
                for (String symbol : ma1vals.keySet()) {
                    if (ma1vals.containsKey(symbol)) {
                        //Calculate ma1s/ma2s
                        rs.put(symbol, ma1vals.get(symbol).divide(ma2vals.get(symbol), RoundingMode.HALF_EVEN));
                        //Divide by Std Dev
                    }
                }

                List<String> selection = getSelection(rs, strategyParams);
                sellLosers(portfolio, rebalanceDate, selection);
                buyWinners(portfolio, strategyParams, rebalanceDate, selection);

                log.info(rebalanceDate + " " + portfolio.getActiveFunds(rebalanceDate) + " " + portfolio.marketValue(rebalanceDate));
            }
        }
    }

    private List<String> getSelection(Map<String, BigDecimal> rs, StrategyServlet.Strategy2Params params) {
        //Rank order
        Ordering<String> valueComparator = Ordering.natural()
                .reverse()
                .onResultOf(Functions.forMap(rs))
                .compound(Ordering.natural());

        List<String> rank = new ArrayList<String>(ImmutableSortedMap.copyOf(rs, valueComparator).keySet());
        return rank.subList(0, params.getCastOff());
    }

    private void sellLosers(Portfolio portfolio, LocalDate rebalanceDate, List<String> selection)
            throws StrategyException {

        for (String symbol : portfolio.getActiveFunds(rebalanceDate)) {
            if (!selection.contains(symbol)) {
                try {
                    executor.sellAll(portfolio, symbol, rebalanceDate);
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
                BigDecimal allocation = availableCash
                        .divide(new BigDecimal(numEmpty), MathContext.DECIMAL32);
                try {
                    executor.buy(portfolio, symbol, rebalanceDate, allocation);
                } catch (PortfolioException e) {
                    e.printStackTrace();
                    throw new StrategyException("Unable to buy winners " + selection +
                            " on " + rebalanceDate +
                            ", current portfolio: " + portfolio.getActiveFunds(rebalanceDate) +
                            ", value: " + portfolio.marketValue(rebalanceDate), e);
                }
            }
            added++;
        }
    }

    private List<LocalDate> getRebalanceDates(LocalDate fromDate, LocalDate toDate, StrategyServlet.Strategy2Params params) {
        return TradingDayUtils.getWeeklySeries(fromDate, toDate, params.getRebalanceFrequency(), true);
    }

}
