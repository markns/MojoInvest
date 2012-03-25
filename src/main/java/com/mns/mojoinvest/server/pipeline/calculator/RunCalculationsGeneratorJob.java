package com.mns.mojoinvest.server.pipeline.calculator;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.model.Fund;
import org.joda.time.LocalDate;

import java.util.List;

public class RunCalculationsGeneratorJob extends Job2<Void, LocalDate, List<Fund>> {

    @Override
    public Value<Void> run(LocalDate date, List<Fund> funds) {

        for (Fund fund : funds) {

            futureCall(new RunCalculationsJob(), immediate(date), immediate(fund));
        }
        return null;
    }


}
