package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.FundDao;
import com.mns.mojoinvest.server.engine.model.Quote;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.logging.Logger;

public class DailyPipeline extends Job1<List<Quote>, LocalDate> {

    private static final Logger log = Logger.getLogger(DailyPipeline.class.getName());

    private static int batchSize = 50;

    @Override
    public Value<List<Quote>> run(LocalDate date) {

        //TODO: Figure out how to inject and serialize DAOs
        ObjectifyFactory factory = ObjectifyService.factory();
        FundDao fundDao = new FundDao(factory);
        fundDao.registerObjects(factory);
        //

        FutureValue<List<String>> symbols = futureCall(new FundFetcherJob());
        FutureValue<List<Quote>> quotes = futureCall(new EODQuoteFetcherJob(), symbols);

        //for each of the parameter combinations (1M, 2M, 6M etc) call
//        futureCall(new RankerJob(), date, quotes);

//        for (int i = 0; i < symbols.size(); i += batchSize) {
//            List<String> batch;
//            if (i + batchSize < symbols.size()) {
//                batch = new ArrayList<String>(symbols.subList(i, i + batchSize));
//            } else {
//                batch = new ArrayList<String>(symbols.subList(i, symbols.size()));
//            }
//
//            futureCall(new QuoteFetcherJob(), immediate(batch));
//        }

//            return immediate(quotes);
//            return futureCall(new QuoteCombinerJob(), futureList(quotes));

        return quotes;
    }


}
