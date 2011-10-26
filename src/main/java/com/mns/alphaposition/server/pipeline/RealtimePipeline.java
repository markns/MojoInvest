package com.mns.alphaposition.server.pipeline;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mns.alphaposition.server.engine.model.FundDao;
import com.mns.alphaposition.server.engine.model.Quote;
import com.mns.alphaposition.server.engine.model.QuoteDao;

import java.util.*;
import java.util.logging.Logger;

public class RealtimePipeline {

    public static class DailyPipeline extends Job1<Collection<Quote>, String> {

        private static final Logger log = Logger.getLogger(DailyPipeline.class.getName());

        private static int batchSize = 50;

        @Override
        public Value<Collection<Quote>> run(String text) {

            //TODO: Figure out how to inject and serialize DAOs
            ObjectifyFactory factory = ObjectifyService.factory();
            FundDao fundDao = new FundDao(factory);
            fundDao.registerObjects(factory);
            //

            List<String> symbols = fundDao.getAllSymbols();
            List<FutureValue<Quote>> quotes = new ArrayList<FutureValue<Quote>>();

            for (int i = 0; i < symbols.size(); i += batchSize) {
                List<String> batch;
                if (i + batchSize < symbols.size()) {
                    batch = new ArrayList<String>(symbols.subList(i, i + batchSize));
                } else {
                    batch = new ArrayList<String>(symbols.subList(i, symbols.size()));
                }

                futureCall(new QuoteFetcherJob(),
                        immediate(batch));
            }

//            return immediate(quotes);
//            return futureCall(new QuoteCombinerJob(), futureList(quotes));

            return null;
        }


    }


    public static class QuoteCombinerJob extends Job1<Collection<Quote>, List<Quote>> {


        @Override
        public Value<Collection<Quote>> run(List<Quote> futureQuotes) {

            ObjectifyFactory factory = ObjectifyService.factory();
            QuoteDao quoteDao = new QuoteDao(factory);
            quoteDao.registerObjects(factory);

            List<Quote> quotes = new ArrayList<Quote>();
            for (Quote quote : futureQuotes) {
                quotes.add(quote);
            }
            Map<Key<Quote>, Quote> quotemap = quoteDao.put(quotes);
            return immediate(quotemap.values());
        }
    }


}
