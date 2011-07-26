package com.mns.alphaposition.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.mns.alphaposition.server.engine.strategy.TradingStrategy;

public class EngineModule extends AbstractModule {

    @Override
    protected void configure() {
        MapBinder<Class, TradingStrategy> mapbinder
                = MapBinder.newMapBinder(binder(), Class.class, TradingStrategy.class);
//        mapbinder.addBinding(MomentumStrategyParams.class).toInstance(new MomentumStrategy());
    }
}
