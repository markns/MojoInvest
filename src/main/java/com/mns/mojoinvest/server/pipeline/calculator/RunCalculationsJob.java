package com.mns.mojoinvest.server.pipeline.calculator;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.calculator.CalculationService;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.pipeline.PipelineHelper;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class RunCalculationsJob extends Job1<String, String> {

    private static final Logger log = Logger.getLogger(RunCalculationsJob.class.getName());

    @Override
    public Value<String> run(String symbol) {

        FundDao fundDao = PipelineHelper.getFundDao();
        QuoteDao quoteDao = PipelineHelper.getQuoteDao();
        CalculatedValueDao cvDao = PipelineHelper.getCalculatedValueDao();

        Fund fund = fundDao.get(symbol);

        LocalDate from;
        if (fund.getLatestCalculationDate() != null) {
            from = fund.getLatestCalculationDate();
        } else {
            from = fund.getEarliestQuoteDate();
        }

        LocalDate to = fund.getLatestQuoteDate();

        if (from == null || to == null) {
            log.warning("Unable to calculate values for " + fund + " - no quotes persisted");
            return immediate("Unable to calculate values for " + fund + " - no quotes persisted");
        }

        List<LocalDate> dates = TradingDayUtils.getEndOfWeekSeries(from, to, 1);
        List<Quote> quotes = quoteDao.get(fund, dates);

        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();

        //Moving averages
        for (int period : Arrays.asList(4, 8, 12, 26, 39, 52)) {
            cvs.addAll(CalculationService.calculateSMA(quotes, period));
        }

        //Standardized ROC
        for (int period : Arrays.asList(4, 8, 12, 26, 39, 52)) {
            cvs.addAll(CalculationService.calculateROC(quotes, period));
        }

        //Standard deviations
        for (int period : Arrays.asList(4, 8, 12, 26, 39, 52)) {
            cvs.addAll(CalculationService.calculateStandardDeviation(quotes, period));
        }

//        cvDao.put(cvs);

        return immediate("Finished calculated values for " + fund);

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


//
//            //RSquared - coefficient of determination
//            for (int period : Arrays.asList(12, 26, 39, 52)) {
//                cvs.addAll(calculationService.calculateRSquared(quoteSeries, period));
//            }

        //Alpha
//            for (int period : Arrays.asList(100)) {
//                cvs.addAll(calculationService.calculateAlpha(quoteSeries, idxReturns, period));
//            }


    }
}
