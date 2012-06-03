package com.mns.mojoinvest.server.engine.result;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.params.Params;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.strategy.DrawDown;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.apache.commons.lang.ArrayUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Years;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.logging.Logger;

public class StrategyResultBuilder {

    private static final Logger log = Logger.getLogger(StrategyResultBuilder.class.getName());

    private final QuoteDao quoteDao;
    private final FundDao fundDao;

    private Portfolio portfolio;
    private Portfolio shadowPortfolio;
    private Map<String, Map<LocalDate, BigDecimal>> additionalResults;

    private Collection<Fund> universe;
    private Map<String, BigDecimal> initCompares = new HashMap<String, BigDecimal>();
    private Map<String, BigDecimal> portfolioCompares = new HashMap<String, BigDecimal>();
    private Params params;

    @Inject
    public StrategyResultBuilder(QuoteDao quoteDao, FundDao fundDao) {
        this.quoteDao = quoteDao;
        this.fundDao = fundDao;
    }

    public StrategyResultBuilder setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
        return this;
    }

    public StrategyResultBuilder setShadowPortfolio(Portfolio shadowPortfolio) {
        this.shadowPortfolio = shadowPortfolio;
        return this;
    }

    public StrategyResultBuilder setParams(Params params) {
        this.params = params;
        return this;
    }

    public StrategyResultBuilder setAdditionalResults(Map<String, Map<LocalDate, BigDecimal>> additionalResults) {
        this.additionalResults = additionalResults;
        return this;
    }

    public void build() throws ResultBuilderException {
        LocalDate fromDate = params.getFromDate();
        LocalDate toDate = params.getToDate();

//        CSVWriter writer = initialiseCsv();
//        writeCsvHeader(universe, writer);

        List<DrawDown> drawDowns = new ArrayList<DrawDown>();
        DrawDown currentDD = null;

        List<LocalDate> rebalanceDates = getRebalanceDates(params);

        LocalDate earliestTransactionDate = portfolio.getTransactions().get(0).getDate();

        for (LocalDate rebalanceDate : rebalanceDates) {

            if (rebalanceDate.isBefore(earliestTransactionDate.minusWeeks(2))) {
                continue;
            }
            currentDD = calculateDrawDowns(drawDowns, currentDD, rebalanceDate, portfolio.marketValue(rebalanceDate));

//            initialiseComparisonsForCsv(universe, rebalanceDate, portfolio.marketValue(rebalanceDate));
//            String[] compares = calculatePercentChangeForCsv(universe, rebalanceDate);
//            writeCsvRow(portfolio, shadowPortfolio, writer, rebalanceDate, portfolio.marketValue(rebalanceDate),
//                    shadowPortfolio.marketValue(rebalanceDate),
//                    additionalResults.get(MomentumStrategy.SHADOW_EQUITY_CURVE).get(rebalanceDate),
//                    compares);
        }

//        flushCsvWriter(writer);

        logParams(params);
        logTrades(portfolio);
        logDrawDowns(drawDowns);
        logCAGR(portfolio, toDate);
    }

    private List<LocalDate> getRebalanceDates(Params params) {
        return TradingDayUtils.getEndOfWeekSeries(params.getFromDate(), params.getToDate(),
                params.getRebalanceFrequency());
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

    private void logParams(Params params) {
        log.info("" + params);
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


    private void logCAGR(Portfolio portfolio, LocalDate toDate)
            throws ResultBuilderException {
        if (portfolio.getTransactions().size() == 0)
            throw new ResultBuilderException("No transactions in portfolio");
        LocalDate fromDate = portfolio.getTransactions().get(0).getDate();

        BigDecimal marketValue = portfolio.marketValue(toDate);
        log.info("Final portfolio value: " + marketValue);
        double base = marketValue.divide(new BigDecimal(portfolio.getParams().getInitialInvestment())).doubleValue();
        double e = 1d / Years.yearsBetween(fromDate, toDate).getYears();
        double cagr = (1 - Math.pow(base, e)) * -100;
        log.info("CAGR: " + cagr + "%");
    }

    private void logTrades(Portfolio portfolio) {
//        for (Transaction transaction : portfolio.getTransactions()) {
//
//            log.fine(transaction + "");
//        }
        log.info("Number of trades: " + portfolio.getTransactions().size());
    }


    /*
     * Csv writing stuff - non production
    */

    private CSVWriter initialiseCsv() {
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter("data/strategy_runs/" + new LocalTime() + ".csv"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("", e);
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

    private static BigDecimal percentageChange(BigDecimal from, BigDecimal to) {
        BigDecimal change = to.subtract(from);
        return change.divide(from, MathContext.DECIMAL32);
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
