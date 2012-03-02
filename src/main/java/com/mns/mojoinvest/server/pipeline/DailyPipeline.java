package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.pipeline.fund.FundFetcherJob;
import com.mns.mojoinvest.server.pipeline.fund.FundUpdaterJob;
import com.mns.mojoinvest.server.pipeline.quote.QuoteUpdaterJob;
import com.mns.mojoinvest.server.pipeline.quote.QuotesFetcherJob;
import com.mns.mojoinvest.server.util.HolidayUtils;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DailyPipeline extends Job1<List<Quote>, LocalDate> {

    private static final Logger log = Logger.getLogger(DailyPipeline.class.getName());

    @Override
    public Value<List<Quote>> run(LocalDate date) {

        List<Value<String>> messages = new ArrayList<Value<String>>();
        if (HolidayUtils.isHoliday(date)) {
            messages.add(immediate("Not running pipeline, today is " + HolidayUtils.get(date)));
            futureCall(new EmailStatusJob(), futureList(messages));
        }

        FutureValue<List<Fund>> funds = futureCall(new FundFetcherJob());
        messages.add(futureCall(new FundUpdaterJob(), funds));
        FutureValue<List<Quote>> quotes = futureCall(new QuotesFetcherJob(), funds, immediate(date));
        messages.add(futureCall(new QuoteUpdaterJob(), quotes));

//        //for each of the parameter combinations (1M, 2M, 6M etc) call
//        for (Integer integer : Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 18, 24)) {
//            RankingParams params = new RankingParams(integer);
//            futureCall(new PerformanceRankingJob(), immediate(date), immediate(params), waitFor(quotesUpdated));
//        }

        //Send email for confirmation of success or failure
        futureCall(new EmailStatusJob(), futureList(messages));

        return null;
    }


}
