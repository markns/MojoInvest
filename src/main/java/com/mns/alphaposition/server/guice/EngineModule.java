package com.mns.alphaposition.server.guice;

import com.google.inject.AbstractModule;
import com.mns.alphaposition.server.engine.execution.Executor;
import com.mns.alphaposition.server.engine.execution.NextTradingDayExecutor;
import com.mns.alphaposition.server.engine.strategy.MomentumStrategy;
import com.mns.alphaposition.server.engine.strategy.RankingStrategy;
import com.mns.alphaposition.server.engine.strategy.SimpleRankingStrategy;
import com.mns.alphaposition.server.engine.strategy.TradingStrategy;

public class EngineModule extends AbstractModule {

    @Override
    protected void configure() {
        //TODO: This map should contain the different strategies available
//        MapBinder<Class, TradingStrategy> mapbinder
//                = MapBinder.newMapBinder(binder(), Class.class, TradingStrategy.class);
//        mapbinder.addBinding(MomentumStrategyParams.class).toInstance(new MomentumStrategy());


        bind(RankingStrategy.class).to(SimpleRankingStrategy.class);
        bind(TradingStrategy.class).to(MomentumStrategy.class);
        bind(Executor.class).to(NextTradingDayExecutor.class);
    }
}
