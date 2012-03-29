package com.mns.mojoinvest.server.guice;

import com.google.inject.AbstractModule;
import com.mns.mojoinvest.server.engine.model.dao.*;

public class StandaloneModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(QuoteDao.class).to(InMemoryQuoteDao.class);
        bind(FundDao.class).to(InMemoryFundDao.class);
        bind(CalculatedValueDao.class).to(InMemoryCalculatedValueDao.class);

    }
}
