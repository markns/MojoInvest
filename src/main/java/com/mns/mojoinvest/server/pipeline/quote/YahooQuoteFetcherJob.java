package com.mns.mojoinvest.server.pipeline.quote;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.google.common.base.Joiner;
import com.mns.mojoinvest.server.engine.model.Fund;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class YahooQuoteFetcherJob extends Job2<String, List<Fund>, LocalDate> {

    private static final Logger log = Logger.getLogger(YahooQuoteFetcherJob.class.getName());

    private static final int BATCH_SIZE = 50;

    @Override
    public Value<String> run(List<Fund> funds, LocalDate date) {

        List<FutureValue<String>> messages = new ArrayList<FutureValue<String>>();
        List<Fund> batch = new ArrayList<Fund>(BATCH_SIZE);

        funds.clear();
        funds.add(new Fund("BARL", "", "", "", true, "", "", "", new LocalDate("1993-03-03")));
        funds.add(new Fund("PXMG", "", "", "", true, "", "", "", new LocalDate("1993-03-03")));
        funds.add(new Fund("RTL", "", "", "", true, "", "", "", new LocalDate("1993-03-03")));


        for (Fund fund : funds) {
            batch.add(fund);
            if (batch.size() == BATCH_SIZE) {
                List<Fund> clone = new ArrayList<Fund>(batch);
                messages.add(futureCall(new YahooQuoteFetcherBatchJob(), immediate(clone), immediate(date)));
                batch.clear();
            }
        }
        if (batch.size() > 0) {
            messages.add(futureCall(new YahooQuoteFetcherBatchJob(), immediate(batch), immediate(date)));
        }

        return futureCall(new MergeMessagesJob(), futureList(messages));
    }


    private static class MergeMessagesJob extends Job1<String, List<String>> {

        @Override
        public Value<String> run(List<String> lists) {
            return immediate(Joiner.on("\n").join(lists));
        }
    }


}
