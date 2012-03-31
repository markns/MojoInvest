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
    private static final String outfile = "data/etf_international_cvs.csv";

    private void run() throws IOException {

        quoteDao.init("data/etf_international_quotes.csv");
        fundDao.init("data/etf_international_funds.csv");
//        quoteDao.init("data/etf_sector_quotes.csv");
//        fundDao.init("data/etf_sector_funds.csv");
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
            LocalDate earliest = quotes.get(0).getDate();
//            LocalDate latest = quotes.get(quotes.size() - 1).getDate();
            LocalDate latest = new LocalDate("2012-03-30");

            List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();

            //Moving averages
            for (int period : Arrays.asList(4, 13, 26, 39, 52)) {
                List<LocalDate> dates = TradingDayUtils.getWeeklySeries(earliest, latest, 1, true);
                List<Quote> weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));
                cvs.addAll(calculationService.calculateSMA(weeklySeries, period));

                dates = TradingDayUtils.getWeeklySeries(earliest, latest.minusDays(1), 1, true);
                weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));
                cvs.addAll(calculationService.calculateSMA(weeklySeries, period));

                dates = TradingDayUtils.getWeeklySeries(earliest, latest.minusDays(2), 1, true);
                weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));
                cvs.addAll(calculationService.calculateSMA(weeklySeries, period));

                dates = TradingDayUtils.getWeeklySeries(earliest, latest.minusDays(3), 1, true);
                weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));
                cvs.addAll(calculationService.calculateSMA(weeklySeries, period));

                dates = TradingDayUtils.getWeeklySeries(earliest, latest.minusDays(4), 1, true);
                weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));
                cvs.addAll(calculationService.calculateSMA(weeklySeries, period));
            }


            //Standardized ROC
            for (int period : Arrays.asList(4, 13, 26, 39, 52)) {
                LocalDate toDate = new LocalDate(latest);
                while (!toDate.isBefore(earliest.plusDays(period))) {
                    Quote fromQuote = quoteDao.get(fund, TradingDayUtils.rollBack(toDate.minusWeeks(period)));
                    Quote toQuote = quoteDao.get(fund, toDate);
                    if (fromQuote != null && toQuote != null) {
                        cvs.add(calculationService.calculateROC(fromQuote, toQuote, period));
                    }
                    toDate = TradingDayUtils.rollBack(toDate.minusDays(1));
                }
            }

            //Standard deviations
            for (int period : Arrays.asList(26, 39, 52)) {
                List<LocalDate> dates = TradingDayUtils.getWeeklySeries(earliest, latest, 1, true);
                List<Quote> weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));
                cvs.addAll(calculationService.calculateStandardDeviation(weeklySeries, period));

                dates = TradingDayUtils.getWeeklySeries(earliest, latest.minusDays(1), 1, true);
                weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));
                cvs.addAll(calculationService.calculateStandardDeviation(weeklySeries, period));

                dates = TradingDayUtils.getWeeklySeries(earliest, latest.minusDays(2), 1, true);
                weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));
                cvs.addAll(calculationService.calculateStandardDeviation(weeklySeries, period));

                dates = TradingDayUtils.getWeeklySeries(earliest, latest.minusDays(3), 1, true);
                weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));
                cvs.addAll(calculationService.calculateStandardDeviation(weeklySeries, period));

                dates = TradingDayUtils.getWeeklySeries(earliest, latest.minusDays(4), 1, true);
                weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, dates));
                cvs.addAll(calculationService.calculateStandardDeviation(weeklySeries, period));
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

}
