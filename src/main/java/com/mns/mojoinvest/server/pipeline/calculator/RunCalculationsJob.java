package com.mns.mojoinvest.server.pipeline.calculator;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.calculator.CalculationService;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.ObjectifyQuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunCalculationsJob extends Job2<Void, LocalDate, Fund> {


    private transient QuoteDao dao;
    private transient CalculatedValueDao cvDao;
    private transient List<String> messages;

    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {

        ObjectifyFactory factory = ObjectifyService.factory();
        this.dao = new ObjectifyQuoteDao(factory);
        this.dao.registerObjects(factory);

        this.cvDao = new CalculatedValueDao(factory);
        this.cvDao.registerObjects(factory);

        messages = new ArrayList<String>();
    }

    @Override
    public Value<Void> run(LocalDate date, Fund fund) {
//        MA - 1, 3, 6, 9, 12 month
//        ROC - 1, 3, 6, 9, 12 month
//        Alpha - 1, 3, 6, 9, 12 month
//        Standard Deviation - 1, 3, 6, 9, 12 month


        CalculationService calculationService = new CalculationService(dao);
        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
        for (int period : Arrays.asList(4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52)) {
            cvs.addAll(calculationService.calculateSMA(fund, date.minusWeeks(period), date, period));
        }
        cvDao.put(cvs);

        return null;
    }
}
