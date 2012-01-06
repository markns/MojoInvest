package com.mns.mojoinvest.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.execution.NextTradingDayExecutor;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioFactory;
import com.mns.mojoinvest.server.engine.portfolio.SimplePortfolio;

public class EngineModule extends AbstractModule {

    @Override
    protected void configure() {
        //TODO: This map should contain the different strategies available
//        MapBinder<Class, TradingStrategy> mapbinder
//                = MapBinder.newMapBinder(binder(), Class.class, TradingStrategy.class);
//        mapbinder.addBinding(MomentumStrategyParams.class).toInstance(new MomentumStrategy());


//        bind(RankingStrategy.class).to(SimpleRankingStrategy.class);
//        bind(TradingStrategy.class).to(MomentumStrategy.class);
        bind(Executor.class).to(NextTradingDayExecutor.class);

        install(new FactoryModuleBuilder()
                .implement(Portfolio.class, SimplePortfolio.class)
                .build(PortfolioFactory.class));

    }
}
