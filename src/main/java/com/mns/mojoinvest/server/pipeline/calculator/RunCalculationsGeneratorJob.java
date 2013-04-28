package com.mns.mojoinvest.server.pipeline.calculator;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job0;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.pipeline.GenericPipelines;
import com.mns.mojoinvest.server.pipeline.ImmediateReturnJob;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class RunCalculationsGeneratorJob extends Job0<String> {

    private static final Logger log = Logger.getLogger(RunCalculationsGeneratorJob.class.getName());

    @Override
    public Value<String> run() {

        List<FutureValue<String>> calculationsUpdated = new ArrayList<FutureValue<String>>();
        FutureValue<String> calcDone = futureCall(new ImmediateReturnJob(), immediate("Start"));

        for (int period : Arrays.asList(4, 8, 12, 26, 39, 52)) {
            calcDone = futureCall(new SMACalculatorJob(), immediate(period), waitFor(calcDone));
            calculationsUpdated.add(calcDone);
            calcDone = futureCall(new ROCCalculatorJob(), immediate(period), waitFor(calcDone));
            calculationsUpdated.add(calcDone);
            calcDone = futureCall(new StdDevCalculatorJob(), immediate(period), waitFor(calcDone));
            calculationsUpdated.add(calcDone);
        }

        calculationsUpdated.add(futureCall(new RunCorrelationCalculatorJob(), immediate(new LocalDate("2013-03-07"))));

        return futureCall(new GenericPipelines.MergeListJob(), futureList(calculationsUpdated));
    }


}