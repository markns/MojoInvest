package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job4;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.pipeline.calculator.RunCalculationsGeneratorJob;
import com.mns.mojoinvest.server.pipeline.fund.ISharesFundFetcherControlJob;
import com.mns.mojoinvest.server.pipeline.quote.ISharesQuoteFetcherControlJob;
import com.mns.mojoinvest.server.util.HolidayUtils;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DailyPipeline extends Job4<Void, LocalDate, String, Boolean, Boolean> {

    private static final Logger log = Logger.getLogger(DailyPipeline.class.getName());

    private static final String USER_EMAIL = "marknuttallsmith@gmail.com";

    @Override
    public Value<Void> run(LocalDate date, String sessionIdStr, Boolean getFunds, Boolean getQuotes) {

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

        FutureValue<String> fundsUpdatedMessage;
        if (getFunds) {
            fundsUpdatedMessage = futureCall(new ISharesFundFetcherControlJob());
        } else {
            fundsUpdatedMessage = futureCall(new ImmediateReturnJob(), immediate("Skipping fund retrieval"));
        }
        //TODO: why does this cause failures - messages.add(fundsUpdatedMessage);

        Value<String> sessionId;
        if (sessionIdStr == null) {
            sessionId = futureCall(new ExternalAgentJob(), immediate(USER_EMAIL));
        } else {
            sessionId = immediate(sessionIdStr);
        }

        FutureValue<String> quotesUpdatedMessage;
        if (getQuotes) {
            quotesUpdatedMessage = futureCall(new ISharesQuoteFetcherControlJob(), sessionId,
                    waitFor(fundsUpdatedMessage));
        } else {
            quotesUpdatedMessage = futureCall(new ImmediateReturnJob(), immediate("Skipping quote retrieval"));
        }
        //TODO: why does this cause failures - messages.add(quotesUpdatedMessage);

        FutureValue<String> calculationsUpdatedMessage = futureCall(new RunCalculationsGeneratorJob(),
                waitFor(quotesUpdatedMessage));


        //TODO: Send email on failure also
        futureCall(new EmailStatusJob(), immediate(USER_EMAIL), futureList(messages), waitFor(calculationsUpdatedMessage));

        return null;
    }

    public String getPipelineHandle() {
        return KeyFactory.keyToString(getPipelineKey());
    }

}
