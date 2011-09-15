package com.mns.alphaposition.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.mns.alphaposition.server.engine.strategy.MomentumStrategy;
import com.mns.alphaposition.server.engine.strategy.TestStrategy;
import com.mns.alphaposition.server.engine.strategy.TradingStrategy;
import com.mns.alphaposition.shared.params.MomentumStrategyParams;
import com.mns.alphaposition.shared.params.StrategyParams;

public class TradingStrategyModule extends AbstractModule {


    public void configure() {
        MapBinder<Class, TradingStrategy> strategyBinder = MapBinder.newMapBinder(binder(),
                Class.class, TradingStrategy.class);
        strategyBinder.addBinding(MomentumStrategyParams.class).to(MomentumStrategy.class);
        strategyBinder.addBinding().to(TestStrategy.class);
    }
}
