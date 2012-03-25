package com.mns.mojoinvest.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.execution.NextTradingDayExecutor;
import com.mns.mojoinvest.server.engine.model.dao.ObjectifyQuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioFactory;
import com.mns.mojoinvest.server.engine.portfolio.SimplePortfolio;

public class EngineModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(QuoteDao.class).to(ObjectifyQuoteDao.class);
        bind(Executor.class).to(NextTradingDayExecutor.class);
        install(new FactoryModuleBuilder()
                .implement(Portfolio.class, SimplePortfolio.class)
                .build(PortfolioFactory.class));
    }
}
