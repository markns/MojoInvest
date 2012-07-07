package com.mns.mojoinvest.server.engine.result;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.inject.Inject;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.DateValue;
import com.google.visualization.datasource.datatable.value.NumberValue;
import com.google.visualization.datasource.datatable.value.TextValue;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.ibm.icu.util.GregorianCalendar;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.params.Params;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.strategy.MomentumStrategy;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.apache.commons.lang.ArrayUtils;
import org.joda.time.LocalDate;
import org.joda.time.Years;

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

    public StrategyResultBuilder setParams(Params params) {
        this.params = params;
        return this;
    }

    public StrategyResultBuilder setAdditionalResults(Map<String, Map<LocalDate, BigDecimal>> additionalResults) {
        this.additionalResults = additionalResults;
        return this;
    }

    public StrategyResult build() throws ResultBuilderException {

        log.fine("Building strategy result");
        List<LocalDate> rebalanceDates = getRebalanceDates(params);

        DataTable dataTable = generateDataTable(rebalanceDates);

        Map<String, Object> stats = new HashMap<String, Object>();
        BigDecimal maxDD = maxDD(rebalanceDates);
        BigDecimal cagr = cagr();
        stats.put("Max DD%", maxDD);
        stats.put("CAGR", cagr);
        stats.put("CAGR/Max DD%", maxDD.divide(cagr, MathContext.DECIMAL32));
        stats.put("Total Return", totalReturn());
        stats.put("Num Trades", portfolio.getTransactions().size());

        return new StrategyResult(dataTable, portfolio.getTransactions(), stats);
    }

    private BigDecimal totalReturn() {
        BigDecimal marketValue = portfolio.marketValue(params.getToDate());
        return marketValue;
    }

    private BigDecimal cagr()
            throws ResultBuilderException {
        if (portfolio.getTransactions().size() == 0)
            throw new ResultBuilderException("No transactions in portfolio");
        LocalDate fromDate = portfolio.getTransactions().get(0).getDate();

        BigDecimal marketValue = portfolio.marketValue(params.getToDate());
        log.info("Final portfolio value: " + marketValue);
        double base = marketValue.divide(new BigDecimal(portfolio.getParams().getInitialInvestment())).doubleValue();
        double e = 1d / Years.yearsBetween(fromDate, params.getToDate()).getYears();
        double cagr = (1 - Math.pow(base, e)) * -100;
        log.info("CAGR: " + cagr + "%");
        return new BigDecimal(cagr);
    }

    private BigDecimal maxDD(List<LocalDate> dates) {
        List<DrawDown> drawDowns = new ArrayList<DrawDown>();
        DrawDown currentDD = null;

        LocalDate earliestTransactionDate = portfolio.getTransactions().get(0).getDate();
        for (LocalDate rebalanceDate : dates) {
            if (rebalanceDate.isBefore(earliestTransactionDate.minusWeeks(2))) {
                continue;
            }
            currentDD = calculateDrawDowns(drawDowns, currentDD, rebalanceDate,
                    portfolio.marketValue(rebalanceDate));
        }
        BigDecimal maxDD = BigDecimal.ZERO;
        for (DrawDown drawDown : drawDowns) {
            if (drawDown.getPctValue().compareTo(maxDD) > 0) {
                maxDD = drawDown.getPctValue();
            }
        }
        log.info("MaxDD: " + maxDD + "%");
        return maxDD;
    }


    public DataTable generateDataTable(List<LocalDate> dates) {

        // Create a data table
        DataTable data = new DataTable();

        ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
        cd.add(new ColumnDescription("Date", ValueType.DATE, "Date"));
        cd.add(new ColumnDescription("Portfolio", ValueType.NUMBER, "Portfolio Value"));
        cd.add(new ColumnDescription("PortfolioTransactions", ValueType.TEXT, "Portfolio Transactions"));
        cd.add(new ColumnDescription("ShadowPortfolio", ValueType.NUMBER, "Shadow Portfolio Value"));
        cd.add(new ColumnDescription("ShadowPortfolioEC", ValueType.NUMBER, "Shadow Portfolio Equity Curve"));

        data.addColumns(cd);

        Map<LocalDate, BigDecimal> spv = additionalResults.get(MomentumStrategy.SHADOW_PORTFOLIO_MARKET_VALUE);
        Map<LocalDate, BigDecimal> sec = additionalResults.get(MomentumStrategy.SHADOW_EQUITY_CURVE);

        SortedMap<LocalDate, TableRow> rows = new TreeMap<LocalDate, TableRow>();
        for (LocalDate date : dates) {
            TableRow row = new TableRow();
            row.addCell(new DateValue(getGregorianCalendar(date)));
            row.addCell(portfolio.marketValue(date).doubleValue());
            row.addCell(TextValue.getNullValue());
            row.addCell(getNullSafeNumberValue(spv, date));
            row.addCell(getNullSafeNumberValue(sec, date));
            rows.put(date, row);
        }

        Map<LocalDate, List<Transaction>> transactionMap = new HashMap<LocalDate, List<Transaction>>();
        for (Transaction transaction : portfolio.getTransactions()) {
            if (!transactionMap.containsKey(transaction.getDate())) {
                transactionMap.put(transaction.getDate(), new ArrayList<Transaction>());
            }
            transactionMap.get(transaction.getDate()).add(transaction);
        }

        for (LocalDate date : transactionMap.keySet()) {

            StringBuilder annotation = new StringBuilder();
            for (Transaction transaction : transactionMap.get(date)) {
                String buySell = "Sell";
                if (transaction instanceof BuyTransaction) {
                    buySell = "Buy";
                }
                annotation.append(buySell).append(" ")
                        .append(transaction.getFund()).append(" ")
                        .append(transaction.getQuantity()).append(" @ ")
                        .append(transaction.getPrice()).append("\n");
            }

            TableRow row = new TableRow();
            row.addCell(new DateValue(getGregorianCalendar(date)));
            row.addCell(portfolio.marketValue(date).doubleValue());
            row.addCell(annotation.toString().trim());
            row.addCell(NumberValue.getNullValue());
            row.addCell(NumberValue.getNullValue());
            rows.put(date, row);
        }

        for (TableRow tableRow : rows.values()) {
            try {
                data.addRow(tableRow);
            } catch (TypeMismatchException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    private NumberValue getNullSafeNumberValue(Map<LocalDate, BigDecimal> spv, LocalDate date) {
        return spv.get(date) == null ? NumberValue.getNullValue() : new NumberValue(spv.get(date).doubleValue());
    }

    private static GregorianCalendar getGregorianCalendar(LocalDate date) {
        GregorianCalendar cal = new GregorianCalendar(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
        cal.setTimeZone(com.ibm.icu.util.TimeZone.getTimeZone("GMT"));
        return cal;
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


    /*
     * Csv writing stuff - non production
    */


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
