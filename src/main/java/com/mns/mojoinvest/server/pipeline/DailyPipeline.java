package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.logging.Logger;

public class DailyPipeline extends Job1<List<Quote>, LocalDate> {

    private static final Logger log = Logger.getLogger(DailyPipeline.class.getName());

    @Override
    public Value<List<Quote>> run(LocalDate date) {

        FutureValue<List<Fund>> funds = futureCall(new FundFetcherJob());
        futureCall(new FundUpdaterJob(), funds);
        FutureValue<List<Quote>> quotes = futureCall(new QuotesFetcherJob(), funds, immediate(date));
        FutureValue<Boolean> done = futureCall(new QuoteUpdaterJob(), quotes);

        //for each of the parameter combinations (1M, 2M, 6M etc) call
        futureCall(new RankerJob(), immediate(date), waitFor(done));
//
//        return quotes;
        return null;
    }


}
