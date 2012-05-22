package com.mns.mojoinvest.server.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * @author Mark Nuttall-Smith
 */
public class MojoGuiceServletContextListener extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new MojoServletModule(),
                new EngineModule(),
                new MustacheModule(),
                new TradingStrategyModule());
    }

}
