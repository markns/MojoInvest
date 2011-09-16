package com.mns.alphaposition.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.mns.alphaposition.server.engine.strategy.MomentumStrategy;
import com.mns.alphaposition.server.engine.strategy.TestStrategy;
import com.mns.alphaposition.server.engine.strategy.TestStrategyParams;
import com.mns.alphaposition.server.engine.strategy.TradingStrategy;
import com.mns.alphaposition.shared.params.MomentumStrategyParams;

public class TradingStrategyModule extends AbstractModule {


    public void configure() {
        MapBinder<Class, TradingStrategy> strategyBinder = MapBinder.newMapBinder(binder(),
                Class.class, TradingStrategy.class);
        strategyBinder.addBinding(MomentumStrategyParams.class).to(MomentumStrategy.class);
        strategyBinder.addBinding(TestStrategyParams.class).to(TestStrategy.class);
    }
}
