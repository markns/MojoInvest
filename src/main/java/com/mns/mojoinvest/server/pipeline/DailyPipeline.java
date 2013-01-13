package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.pipeline.fund.FundUpdaterJob;
import com.mns.mojoinvest.server.pipeline.fund.ISharesFundFetcherJob;
import com.mns.mojoinvest.server.pipeline.quote.YahooQuoteFetcherJob;
import com.mns.mojoinvest.server.util.HolidayUtils;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DailyPipeline extends Job1<Void, LocalDate> {

    private static final Logger log = Logger.getLogger(DailyPipeline.class.getName());

    @Override
    public Value<Void> run(LocalDate date) {

        List<Value<String>> messages = new ArrayList<Value<String>>();

        messages.add(immediate("Daily pipeline '" + getPipelineHandle() + "' started for date " + date));
        messages.add(immediate("Pipeline console available at /_ah/pipeline/status.html?root=" + getPipelineHandle()));

        if (HolidayUtils.isHoliday(date)) {
            String message = "Not running pipeline, today is " + HolidayUtils.get(date);
            log.info(message);
            messages.add(immediate(message));
            futureCall(new EmailStatusJob(), futureList(messages));
            return null;
        }

        //TODO: Delete pipeline job records more than one week old

        FutureValue<List<Fund>> funds = futureCall(new ISharesFundFetcherJob());
        FutureValue<String> fundsUpdatedMessage = futureCall(new FundUpdaterJob(), funds);
        messages.add(fundsUpdatedMessage);
//        FutureValue<String> fundsUpdatedMessage = futureCall(new ImmediateReturnJob());
        messages.add(futureCall(new YahooQuoteFetcherJob(), immediate(date), waitFor(fundsUpdatedMessage)));

//        futureCall(new RunCalculationsGeneratorJob(), immediate(date), funds);
//        //for each of the parameter combinations (1M, 2M, 6M etc) call
//        for (Integer integer : Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 18, 24)) {
//            RankingParams params = new RankingParams(integer);
//            futureCall(new PerformanceRankingJob(), immediate(date), immediate(params), waitFor(quotesUpdated));
//        }

        //TODO: Send email on failure also
        futureCall(new EmailStatusJob(), futureList(messages));

        return null;
    }

    public String getPipelineHandle() {
        return KeyFactory.keyToString(getPipelineKey());
    }

}
