package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.calculator.RankingCalculator;
import com.mns.mojoinvest.server.engine.model.Ranking;
import com.mns.mojoinvest.server.engine.model.RankingParams;
import com.mns.mojoinvest.server.engine.model.dao.ObjectifyQuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.RankingDao;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class PerformanceRankingJob extends Job2<Ranking, LocalDate, RankingParams> {

    public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Override
    public Value<Ranking> run(LocalDate date, RankingParams params) {

        //TODO: Figure out how to inject and serialize DAOs
        ObjectifyFactory factory = ObjectifyService.factory();

        QuoteDao quoteDao = new ObjectifyQuoteDao(factory);
        quoteDao.registerObjects(factory);

        RankingDao rankingDao = new RankingDao(factory);
        rankingDao.registerObjects(factory);
        //

        RankingCalculator calculator = new RankingCalculator(quoteDao);
        Ranking ranking = calculator.rank(date, params);
        rankingDao.put(ranking);

        return immediate(ranking);

    }


}
