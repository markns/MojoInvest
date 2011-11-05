package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class QuoteCombinerJob extends Job1<Collection<Quote>, List<Quote>> {


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
