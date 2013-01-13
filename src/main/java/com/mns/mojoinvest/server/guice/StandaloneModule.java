package com.mns.mojoinvest.server.guice;

import com.google.inject.AbstractModule;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.inmemory.InMemoryCalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.inmemory.InMemoryFundDao;
import com.mns.mojoinvest.server.engine.model.dao.inmemory.InMemoryQuoteDao;

public class StandaloneModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(QuoteDao.class).to(InMemoryQuoteDao.class);
        bind(FundDao.class).to(InMemoryFundDao.class);
        bind(CalculatedValueDao.class).to(InMemoryCalculatedValueDao.class);

    }
}
