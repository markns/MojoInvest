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
import java.util.List;
import java.util.logging.Logger;

public class ROCCalculatorJob extends Job1<String, Integer> {

    private static final Logger log = Logger.getLogger(ROCCalculatorJob.class.getName());

    @Override
    public Value<String> run(Integer period) {

        log.fine("Running " + this.getClass().getSimpleName() + " for " + period);

        FundDao fundDao = PipelineHelper.getFundDao();
        QuoteDao quoteDao = PipelineHelper.getQuoteDao();
        CalculatedValueDao cvDao = PipelineHelper.getCalculatedValueDao();

        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();

        for (Fund fund : fundDao.list()) {

            if (fund.getSymbol().equals("EUCF")) {
                log.warning("Skipping EUCF as it causes problems");
                continue;
            }


            LocalDate from;
            if (fund.getLatestCalculationDate() != null) {
                from = fund.getLatestCalculationDate();
            } else {
                from = fund.getEarliestQuoteDate();
            }

            LocalDate to = fund.getLatestQuoteDate();

            if (from == null || to == null) {
                log.warning("Unable to calculate values for " + fund + " - no quotes persisted");
                continue;
            }

            List<LocalDate> dates = TradingDayUtils.getEndOfWeekSeries(from, to, 1);
            List<Quote> quotes = quoteDao.get(fund, dates);

            cvs.addAll(CalculationService.calculateROC(quotes, period));
        }

        cvDao.put("ROC|" + period, cvs);

        return immediate("Finished ROCCalculator for " + period);

    }

}
