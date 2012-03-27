package com.mns.mojoinvest.server.engine.strategy;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.portfolio.Lot;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioException;
import com.mns.mojoinvest.server.engine.portfolio.Position;
import com.mns.mojoinvest.server.servlet.StrategyServlet;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import com.mns.mojoinvest.shared.params.BacktestParams;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MomentumStrategy2 {

    private static final Logger log = Logger.getLogger(MomentumStrategy2.class.getName());

    private final Executor executor;
    private final QuoteDao quoteDao;
    private final FundDao fundDao;

    private final RelativeStrengthCalculator relativeStrengthCalculator;

    @Inject
    public MomentumStrategy2(RelativeStrengthCalculator relativeStrengthCalculator,
                             Executor executor, QuoteDao quoteDao, FundDao fundDao) {
        this.relativeStrengthCalculator = relativeStrengthCalculator;
        this.executor = executor;
        this.quoteDao = quoteDao;
        this.fundDao = fundDao;
    }

    public void execute(Portfolio portfolio, BacktestParams backtestParams,
                        Collection<Fund> universe, StrategyServlet.Strategy2Params strategyParams)
            throws StrategyException {

        LocalDate fromDate = new LocalDate(backtestParams.getFromDate());
        LocalDate toDate = new LocalDate(backtestParams.getToDate());

        if (fromDate.isAfter(toDate))
            throw new StrategyException("From date cannot be after to date");

        List<LocalDate> rebalanceDates = getRebalanceDates(fromDate, toDate, strategyParams);

        List<Map<String, BigDecimal>> strengths = relativeStrengthCalculator
                .getRelativeStrengths(universe, strategyParams, rebalanceDates);

//        for (int i = 0; i < rebalanceDates.size(); i++) {
//            LocalDate rebalanceDate = rebalanceDates.get(i);
//            Map<String, BigDecimal> rs = strengths.get(i);
//            List<String> selection = getSelection(rs, strategyParams);
//            //Should be possible to do a cache warming load here
//        }

        for (int i = 0; i < rebalanceDates.size(); i++) {

            LocalDate rebalanceDate = rebalanceDates.get(i);
            Map<String, BigDecimal> rs = strengths.get(i);

            List<String> selection = getSelection(rs, strategyParams);


            //if portfolio.marketValue(rebalanceDate) is below the x period moving average (equity curve)
            //  Portfolio realPortfolio = portfolio
            //  Portfolio portfolio = realPortfolio.clone();

            sellLosers(portfolio, rebalanceDate, selection);
            buyWinners(portfolio, strategyParams, rebalanceDate, selection);

            log.info(rebalanceDate + " " + portfolio.getActiveFunds(rebalanceDate) + " " +
                    portfolio.marketValue(rebalanceDate));
        }
        logNumTrades(portfolio);
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
                try {
                    BigDecimal allocation = availableCash
                            .divide(new BigDecimal(numEmpty), MathContext.DECIMAL32);
                    executor.buy(portfolio, symbol, rebalanceDate, allocation);
                    added++;
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
