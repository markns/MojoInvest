package com.mns.mojoinvest.server.pipeline.quote;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;

import java.util.List;
import java.util.logging.Logger;

public class QuoteUpdaterJob extends Job1<Boolean, List<Quote>> {

    private static final Logger log = Logger.getLogger(QuoteUpdaterJob.class.getName());

    @Override
    public Value<Boolean> run(List<Quote> quotes) {

        //TODO: Figure out how to inject and serialize DAOs
        ObjectifyFactory factory = ObjectifyService.factory();
        QuoteDao dao = new QuoteDao(factory);
        dao.registerObjects(factory);
        //

        log.info("Saving " + quotes.size() + " quotes");
        dao.put(quotes);

        return immediate(true);
    }
}
