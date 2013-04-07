package com.mns.mojoinvest.server.pipeline.calculator;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job0;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.pipeline.GenericPipelines;
import com.mns.mojoinvest.server.pipeline.PipelineHelper;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RunCalculationsGeneratorJob extends Job0<String> {

    private static final Logger log = Logger.getLogger(RunCalculationsGeneratorJob.class.getName());

    @Override
    public Value<String> run() {
        List<FutureValue<String>> quotesUpdated = new ArrayList<FutureValue<String>>();

        FundDao dao = PipelineHelper.getFundDao();
        for (Fund fund : dao.list()) {
            quotesUpdated.add(futureCall(new RunCalculationsJob(), immediate(fund.getSymbol())));
        }

        quotesUpdated.add(futureCall(new RunCorrelationCalculatorJob(), immediate(new LocalDate("2013-03-07"))));

        return futureCall(new GenericPipelines.MergeListJob(), futureList(quotesUpdated));
    }

}