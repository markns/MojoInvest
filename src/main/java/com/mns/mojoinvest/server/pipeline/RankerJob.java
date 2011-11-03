package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.model.Quote;
import org.joda.time.LocalDate;

import java.util.List;

public class RankerJob extends Job2<Object, LocalDate, List<Quote>> {

    @Override
    public Value<Object> run(LocalDate param1, List<Quote> param2) {
        return null;
    }
}
