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
import org.joda.time.LocalDate;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OfflineCalculatorTool {

    public static void main(String[] args) throws IOException {
        OfflineCalculatorTool tool = new OfflineCalculatorTool();
        tool.run();
    }

    private final InMemoryQuoteDao quoteDao = new InMemoryQuoteDao();
    private final InMemoryFundDao fundDao = new InMemoryFundDao();

    private final CalculationService calculationService = new CalculationService();

    //    private static final String outfile = "data/etf_international_cvs.csv";
//    private static final String outfile = "data/etf_sector_cvs.csv";
//    private static final String outfile = "data/etf_asset_alloc_cvs.csv";
    private static final String outfile = "data/ishares_cvs.csv";
//    private static final String outfile = "data/fidelity_cvs.csv";

    private void run() throws IOException {

//        quoteDao.init("data/etf_international_quotes.csv");
//        fundDao.init("data/etf_international_funds.csv");
//        quoteDao.init("data/etf_sector_quotes.csv");
//        fundDao.init("data/etf_sector_funds.csv");
//        quoteDao.init("data/etf_asset_alloc_quotes.csv");
//        fundDao.init("data/etf_asset_alloc_funds.csv");
        quoteDao.init("data/ishares_quotes.csv");
        fundDao.init("data/ishares_funds.csv");
//        quoteDao.init("data/fidelity_quotes.csv", "data/fidelity_quotes_missing.csv");
//        fundDao.init("data/fidelity_funds.csv");

        CSVWriter writer = new CSVWriter(new FileWriter(outfile));

        for (Fund fund : fundDao.getAll()) {

//            if (!fund.getSymbol().equals("FSCGX"))
//                continue;
            List<Quote> quotes = quoteDao.query(fund.getSymbol());
            if (quotes == null)
                continue;
            QuoteUtils.sortByDateAsc(quotes);

            if (quotes.size() == 0)
                continue;

            LocalDate earliest = quotes.get(0).getDate();
//            LocalDate latest = quotes.get(quotes.size() - 1).getDate();
            LocalDate latest = new LocalDate("2012-03-30");

            List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();

            //Moving averages
            for (int period : Arrays.asList(4, 8, 12, 15, 26, 39, 52)) {
                List<LocalDate> dates = TradingDayUtils.getEndOfWeekSeries(earliest, latest, 1);
                List<Quote> weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));
                cvs.addAll(calculationService.calculateSMA(weeklySeries, period));
            }


            //Standardized ROC
            for (int period : Arrays.asList(4, 12, 26, 39, 52)) {
                List<LocalDate> dates = TradingDayUtils.getEndOfWeekSeries(earliest, latest, 1);
                List<Quote> weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));
                cvs.addAll(calculationService.calculateROC(weeklySeries, period));
            }

            //Standard deviations
            for (int period : Arrays.asList(26, 39, 52)) {
                List<LocalDate> dates = TradingDayUtils.getEndOfWeekSeries(earliest, latest, 1);
                List<Quote> weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));
                cvs.addAll(calculationService.calculateStandardDeviation(weeklySeries, period));
            }

            for (int period : Arrays.asList(13, 26, 39, 52)) {
//            for (int period : Arrays.asList(13)) {
                List<LocalDate> dates = TradingDayUtils.getEndOfWeekSeries(earliest, latest, 1);
                List<Quote> weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));
                cvs.addAll(calculationService.calculateRSquared(weeklySeries, period));
            }

            System.out.println(fund + " " + earliest + " " + latest + " " + cvs.size());
//
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

}
