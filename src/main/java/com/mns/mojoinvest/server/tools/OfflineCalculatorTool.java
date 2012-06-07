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
//        String outfile = "data/etf_asset_alloc_cvs.csv";
//        quoteDao.init("data/ishares_quotes.csv", "data/ishares_quotes_missing.csv", "data/GSPC.csv");
//        fundDao.init("data/ishares_funds.csv");
//        String outfile = "data/ishares_cvs.csv";
//        quoteDao.init("data/fidelity_quotes.csv", "data/fidelity_quotes_missing.csv", "data/etf_quotes_compare.csv");
//        fundDao.init("data/fidelity_funds.csv");
//        String outfile = "data/fidelity_cvs_av.csv";

        CSVWriter writer = new CSVWriter(new FileWriter(outfile));

        LocalDate latest = new LocalDate("2012-03-30");
//        LocalDate latest = new LocalDate("2012-05-02");
        NavigableMap<LocalDate, BigDecimal> idxReturns = getIndexReturns("GSPC", latest);


        for (Fund fund : fundDao.getAll()) {

            List<Quote> quotes = quoteDao.query(fund.getSymbol());
            if (quotes == null)
                continue;
            QuoteUtils.sortByDateAsc(quotes);

            if (quotes.size() == 0)
                continue;

            LocalDate earliest = quotes.get(0).getDate();

            List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
            List<LocalDate> dates = TradingDayUtils.getEndOfWeekSeries(earliest, latest, 1);
            List<Quote> quoteSeries = new ArrayList<Quote>(quoteDao.get(fund, dates));

            QuoteUtils.sortByDateAsc(quoteSeries);
//            List<Quote> quoteSeries = new ArrayList<Quote>(dates.size());
//            for (LocalDate date : dates) {
//                List<LocalDate> thisWeek = new ArrayList<LocalDate>(5);
//                while (date.getDayOfWeek() != DateTimeConstants.SUNDAY) {
//                    if (!HolidayUtils.isHoliday(date))
//                        thisWeek.add(date);
//                    date = date.minusDays(1);
//                }
//                List<Quote> weeklySeries = new ArrayList<Quote>(quoteDao.get(fund, thisWeek));
//                QuoteUtils.sortByDateDesc(weeklySeries);
//
//                if (weeklySeries.size() > 0) {
//                    BigDecimal avClose = BigDecimal.ZERO;
//                    for (Quote quote : weeklySeries) {
//                        avClose = avClose.add(quote.getAdjClose());
//                    }
//                    avClose = avClose.divide(new BigDecimal(weeklySeries.size()), MathContext.DECIMAL32);
//                    Quote copy = weeklySeries.get(0);
//                    quoteSeries.add(new Quote(copy.getSymbol(), copy.getDate(), copy.getOpen(), copy.getHigh(), copy.getLow(),
//                            copy.getClose(), null, null, copy.getVolume(), avClose, copy.isRolled()));
//                }
//            }


//            //Moving averages
            for (int period : Arrays.asList(4, 8, 12, 26, 39, 52)) {
                cvs.addAll(calculationService.calculateSMA(quoteSeries, period));
            }
//
//            //Standardized ROC
            for (int period : Arrays.asList(4, 8, 12, 26, 39, 52)) {
                cvs.addAll(calculationService.calculateROC(quoteSeries, period));
            }
//
//            //Standard deviations
            for (int period : Arrays.asList(12, 26, 39, 52)) {
                cvs.addAll(calculationService.calculateStandardDeviation(quoteSeries, period));
            }
//
//            //RSquared - coefficient of determination
            for (int period : Arrays.asList(12, 26, 39, 52)) {
                cvs.addAll(calculationService.calculateRSquared(quoteSeries, period));
            }

            //Alpha
            for (int period : Arrays.asList(100)) {
                cvs.addAll(calculationService.calculateAlpha(quoteSeries, idxReturns, period));
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

    private NavigableMap<LocalDate, BigDecimal> getIndexReturns(String idx, LocalDate latest) {

        Fund index = new Fund(idx, "", "", "", true, "", "", "", new LocalDate());
        List<Quote> idxQuotes = quoteDao.query(index.getSymbol());
        QuoteUtils.sortByDateAsc(idxQuotes);

        LocalDate earliest = idxQuotes.get(0).getDate();

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
