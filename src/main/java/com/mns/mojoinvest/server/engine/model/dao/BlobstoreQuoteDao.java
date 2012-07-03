package com.mns.mojoinvest.server.engine.model.dao;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import org.apache.commons.lang.NotImplementedException;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BlobstoreQuoteDao implements QuoteDao {

    @Override
    public void registerObjects(ObjectifyFactory ofyFactory) {
        throw new NotImplementedException();
    }

    @Override
    public Key<Quote> put(Quote quote) {
        throw new NotImplementedException();
    }

    @Override
    public Map<Key<Quote>, Quote> put(Iterable<Quote> quotes) {
        throw new NotImplementedException();
    }

    @Override
    public Quote get(String symbol, LocalDate date) {
        return null;
    }

    @Override
    public Quote get(Fund fund, LocalDate date) {
        return get(fund.getSymbol(), date);
    }

    @Override
    public Collection<Quote> get(Fund fund, Collection<LocalDate> dates) {
        return null;
    }

    @Override
    public Collection<Quote> get(List<Key<Quote>> keys) {
        return null;
    }

    @Override
    public Collection<Quote> get(Collection<String> symbols, Collection<LocalDate> dates) {
        return null;
    }

    @Override
    public List<Quote> query(String symbol) {
        throw new NotImplementedException();
    }

    @Override
    public List<Quote> query(LocalDate date) {
        throw new NotImplementedException();
    }
}
