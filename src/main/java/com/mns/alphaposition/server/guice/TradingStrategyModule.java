package com.mns.alphaposition.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.mns.alphaposition.server.engine.strategy.MomentumStrategy;
import com.mns.alphaposition.server.engine.strategy.TradingStrategy;
import com.mns.alphaposition.server.handler.TestStrategy;

public class TradingStrategyModule extends AbstractModule {


    public void configure() {
        Multibinder<TradingStrategy> strategyBinder = Multibinder.newSetBinder(binder(), TradingStrategy.class);
        strategyBinder.addBinding().to(MomentumStrategy.class);
        strategyBinder.addBinding().to(TestStrategy.class);
    }
}
