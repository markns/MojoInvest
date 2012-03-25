package com.mns.mojoinvest.server.pipeline.calculator;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.calculator.SMACalculator;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RunCalculationsJob extends Job2<Void, LocalDate, Fund> {


    private transient QuoteDao dao;
    private transient CalculatedValueDao cvDao;
    private transient List<String> messages;

    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {

        ObjectifyFactory factory = ObjectifyService.factory();
        this.dao = new QuoteDao(factory);
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

        List<LocalDate> weeks4 = TradingDayUtils.getWeeklySeries(date, 4, true);
        Collection<Quote> quotes = dao.get(fund, weeks4);
        assert quotes.size() == 4 : "Expected quote list of 4 elements";
        SMACalculator smaCalculator = new SMACalculator(4);
        for (Quote quote : quotes) {
            smaCalculator.newNum(quote.getAdjClose());
        }
        BigDecimal avg = smaCalculator.getAvg();
        CalculatedValue cv = new CalculatedValue(date + "|" + fund + "|SMA|4", avg);

        cvDao.put(cv);

        return null;
    }
}
