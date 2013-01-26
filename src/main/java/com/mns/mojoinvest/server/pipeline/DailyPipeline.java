package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.pipeline.quote.ISharesQuoteFetcherControlJob;
import com.mns.mojoinvest.server.util.HolidayUtils;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DailyPipeline extends Job2<Void, LocalDate, String> {

    private static final Logger log = Logger.getLogger(DailyPipeline.class.getName());

    private static final String USER_EMAIL = "marknuttallsmith@gmail.com";

    @Override
    public Value<Void> run(LocalDate date, String sessionIdStr) {

        List<Value<String>> messages = new ArrayList<Value<String>>();

        messages.add(immediate("Daily pipeline '" + getPipelineHandle() + "' started for date " + date));
        messages.add(immediate("Pipeline console available at /_ah/pipeline/status.html?root=" + getPipelineHandle()));

        if (HolidayUtils.isHoliday(date)) {
            String message = "Not running pipeline, today is " + HolidayUtils.get(date);
            log.info(message);
            messages.add(immediate(message));
            futureCall(new EmailStatusJob(), immediate(USER_EMAIL), futureList(messages));
            return null;
        }

        //TODO: Delete pipeline job records more than one week old

//        FutureValue<String> fundsUpdatedMessage = futureCall(new ISharesFundFetcherControlJob());
        FutureValue<String> fundsUpdatedMessage = futureCall(new ImmediateReturnJob());

        Value<String> sessionId;
        if (sessionIdStr != null) {
            sessionId = immediate(sessionIdStr);
        } else {
            sessionId = futureCall(new ExternalAgentJob(), immediate(USER_EMAIL));
        }

        FutureValue<String> quotesUpdatedMessage = futureCall(new ISharesQuoteFetcherControlJob(), sessionId, waitFor(fundsUpdatedMessage));


//        futureCall(new RunCalculationsGeneratorJob(), immediate(date), funds);
//        //for each of the parameter combinations (1M, 2M, 6M etc) call
//        for (Integer integer : Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 18, 24)) {
//            RankingParams params = new RankingParams(integer);
//            futureCall(new PerformanceRankingJob(), immediate(date), immediate(params), waitFor(quotesUpdated));
//        }

        //TODO: Send email on failure also
        futureCall(new EmailStatusJob(), immediate(USER_EMAIL), futureList(messages), waitFor(quotesUpdatedMessage));

        return null;
    }

    public String getPipelineHandle() {
        return KeyFactory.keyToString(getPipelineKey());
    }

}
