package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class QuotesFetcherJob extends Job2<List<Quote>, List<Fund>, LocalDate> {

    private static final Logger log = Logger.getLogger(QuotesFetcherJob.class.getName());

    private static final int BATCH_SIZE = 50;

    @Override
    public Value<List<Quote>> run(List<Fund> funds, LocalDate date) {

        List<FutureValue<List<Quote>>> quoteLists = new ArrayList<FutureValue<List<Quote>>>();
        List<Fund> batch = new ArrayList<Fund>(BATCH_SIZE);

        for (Fund fund : funds) {
            batch.add(fund);
            if (batch.size() == BATCH_SIZE) {
                List<Fund> clone = new ArrayList<Fund>(batch);
                quoteLists.add(futureCall(new QuoteBatchJob(), immediate(clone), immediate(date)));
                batch.clear();
            }
        }
        if (batch.size() > 0) {
            quoteLists.add(futureCall(new QuoteBatchJob(), immediate(batch), immediate(date)));
        }

        return futureCall(new MergeQuoteListJob(), futureList(quoteLists));
    }


    public static class QuoteBatchJob extends Job2<List<Quote>, List<Fund>, LocalDate> {

        private static final Logger log = Logger.getLogger(QuoteBatchJob.class.getName());

        @Override
        public Value<List<Quote>> run(List<Fund> funds, LocalDate date) {
            List<FutureValue<Quote>> quotes = new ArrayList<FutureValue<Quote>>();
            log.info("Attempting to retrieve quotes for " + funds.size() + " funds");
            for (Fund fund : funds) {
                quotes.add(futureCall(new QuoteFetcherJob(), immediate(fund), immediate(date)));
            }
            return futureList(quotes);
        }
    }

    private static class MergeQuoteListJob extends Job1<List<Quote>, List<List<Quote>>> {

        @Override
        public Value<List<Quote>> run(List<List<Quote>> lists) {
            List<Quote> quotes = new ArrayList<Quote>();
            for (List<Quote> list : lists) {
                quotes.addAll(list);
            }
            return immediate(quotes);
        }
    }



}
