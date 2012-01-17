package com.mns.mojoinvest.server.engine.strategy;

import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.execution.NextTradingDayExecutor;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDaoImpl;
import com.mns.mojoinvest.server.engine.model.dao.RankingDao;

public class MomentumStrategyTests {

    private final QuoteDaoImpl quoteDao = new QuoteDaoImpl(ObjectifyService.factory());

    private final RankingDao rankingDao = new RankingDao(ObjectifyService.factory());

    private final FundDao fundDao = new FundDao(ObjectifyService.factory());


    private final Executor executor = new NextTradingDayExecutor(quoteDao);


    MomentumStrategy strategy = new MomentumStrategy(executor, rankingDao, fundDao);


}
