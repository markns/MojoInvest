package com.mns.mojoinvest.server.tools;

import au.com.bytecode.opencsv.CSVWriter;
import com.mns.mojoinvest.server.engine.calculator.CalculationService;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.InMemoryFundDao;
import com.mns.mojoinvest.server.engine.model.dao.InMemoryQuoteDao;
import com.mns.mojoinvest.server.util.QuoteUtils;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.apache.commons.math.stat.regression.SimpleRegression;
import org.joda.time.LocalDate;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

public class OfflineCalculatorTool {

    public static void main(String[] args) throws IOException {
        OfflineCalculatorTool tool = new OfflineCalculatorTool();
        tool.run();
    }

    private final InMemoryQuoteDao quoteDao = new InMemoryQuoteDao();
    private final InMemoryFundDao fundDao = new InMemoryFundDao();

    private final CalculationService calculationService = new CalculationService();


    private void run() throws IOException {

//        quoteDao.init("data/etf_international_quotes.csv", "data/etf_quotes_compare.csv");
//        fundDao.init("data/etf_international_funds.csv");
//        String outfile = "data/etf_international_cvs.csv";

        quoteDao.init("data/etf_sector_quotes.csv", "data/etf_quotes_compare.csv");
        fundDao.init("data/etf_sector_funds.csv");
        String outfile = "data/etf_sector_cvs.csv";
//        quoteDao.init("data/etf_asset_alloc_quotes.csv", "data/etf_quotes_compare.csv");
//        fundDao.init("data/etf_asset_alloc_funds.csv");
//  String outfile = "data/etf_asset_alloc_cvs.csv";
//        quoteDao.init("data/ishares_quotes.csv", "data/ishares_quotes_missing.csv", "data/etf_quotes_compare.csv");
//        fundDao.init("data/ishares_funds.csv");
//        String outfile = "data/ishares_cvs.csv";
//        quoteDao.init("data/fidelity_quotes.csv", "data/fidelity_quotes_missing.csv", "data/etf_quotes_compare.csv");
//        fundDao.init("data/fidelity_funds.csv");
//        String outfile = "data/fidelity_cvs.csv";

        CSVWriter writer = new CSVWriter(new FileWriter(outfile));

        NavigableMap<LocalDate, BigDecimal> idxReturns = getIndexReturns("GSPC");

        for (Fund fund : fundDao.getAll()) {

            List<Quote> quotes = quoteDao.query(fund.getSymbol());
            if (quotes == null)
                continue;
            QuoteUtils.sortByDateAsc(quotes);

            if (quotes.size() == 0)
                continue;

            LocalDate earliest = quotes.get(0).getDate();
            LocalDate latest = new LocalDate("2012-03-30");
            List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
            List<LocalDate> dates = TradingDayUtils.getEndOfWeekSeries(earliest, latest, 1);
            List<Quote> weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));

//            //Moving averages
            for (int period : Arrays.asList(4, 8, 12, 26, 39, 52)) {
                cvs.addAll(calculationService.calculateSMA(weeklySeries, period));
            }
//
//            //Standardized ROC
            for (int period : Arrays.asList(4, 8, 12, 26, 39, 52)) {
                cvs.addAll(calculationService.calculateROC(weeklySeries, period));
            }
//
//            //Standard deviations
            for (int period : Arrays.asList(12, 26, 39, 52)) {
                cvs.addAll(calculationService.calculateStandardDeviation(weeklySeries, period));
            }
//
//            //RSquared - coefficient of determination
            for (int period : Arrays.asList(12, 26, 39, 52)) {
                cvs.addAll(calculationService.calculateRSquared(weeklySeries, period));
            }

            //Alpha
            NavigableMap<LocalDate, BigDecimal> returns = new TreeMap<LocalDate, BigDecimal>();
            Quote from = null;
            for (Quote to : weeklySeries) {
                if (from != null) {
                    returns.put(to.getDate(), percentageReturn(from.getAdjClose(), to.getAdjClose()));
                }
                from = to;
            }
            int period = 100;
            for (int i = 1; i + period < weeklySeries.size(); i++) {
                NavigableMap<LocalDate, BigDecimal> returnsPeriod = returns.subMap(weeklySeries.get(i).getDate(), true,
                        weeklySeries.get(i + period).getDate(), false);
                NavigableMap<LocalDate, BigDecimal> idxReturnsPeriod = idxReturns.subMap(weeklySeries.get(i).getDate(), true,
                        weeklySeries.get(i + period).getDate(), false);
                if (returnsPeriod.size() != idxReturnsPeriod.size()) {
                    throw new RuntimeException(returnsPeriod.size() + " " + idxReturnsPeriod.size());
                }
                Iterator<BigDecimal> returnsIter = returnsPeriod.values().iterator();
                Iterator<BigDecimal> idxReturnsIter = idxReturnsPeriod.values().iterator();

                SimpleRegression regression = new SimpleRegression();
                while (returnsIter.hasNext() && idxReturnsIter.hasNext()) {
                    regression.addData(returnsIter.next().doubleValue(), idxReturnsIter.next().doubleValue());
                }
                double intercept = regression.getIntercept();
                cvs.add(new CalculatedValue(returnsPeriod.lastEntry().getKey(), fund.getSymbol(), "ALPHA", period, intercept));
            }


            System.out.println(fund + " " + earliest + " " + latest + " " + cvs.size());

            int i = 0;
            for (CalculatedValue cv : cvs) {
                writer.writeNext(cv.toStrArr());
                if (i++ == 5000)
                    writer.flush();

            }
            writer.flush();
        }
        writer.close();

    }

    private NavigableMap<LocalDate, BigDecimal> getIndexReturns(String idx) {

        Fund index = new Fund(idx, "", "", "", true, "", "", "", new LocalDate());
        List<Quote> idxQuotes = quoteDao.query(index);
        QuoteUtils.sortByDateAsc(idxQuotes);

        LocalDate earliest = idxQuotes.get(0).getDate();
        LocalDate latest = new LocalDate("2012-03-30");

        List<LocalDate> dates = TradingDayUtils.getEndOfWeekSeries(earliest, latest, 1);
        List<Quote> weeklySeries = new ArrayList<Quote>(quoteDao.get(index, dates));

        NavigableMap<LocalDate, BigDecimal> idxReturns = new TreeMap<LocalDate, BigDecimal>();

        Quote from = null;
        for (Quote to : weeklySeries) {
            if (from != null) {
                idxReturns.put(to.getDate(), percentageReturn(from.getAdjClose(), to.getAdjClose()));
            }
            from = to;
        }
        return idxReturns;
    }

    private BigDecimal percentageReturn(BigDecimal from, BigDecimal to) {
        BigDecimal change = to.subtract(from);
        return change.divide(from, MathContext.DECIMAL32);
    }

}
