package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import org.joda.time.LocalDate;

public class QuoteRollerJob extends Job1<Object, LocalDate> {

    @Override
    public Value<Object> run(LocalDate param1) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
