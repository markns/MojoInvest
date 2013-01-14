package com.mns.mojoinvest.server.engine.model.dao;

import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import org.joda.time.LocalDate;

import java.util.Collection;

public interface QuoteDao {

    void registerObjects(ObjectifyFactory ofyFactory);

    void put(Iterable<Quote> quotes) throws DataAccessException;

    Quote get(String symbol, LocalDate date) throws QuoteUnavailableException, DataAccessException;

    Quote get(Fund fund, LocalDate date) throws QuoteUnavailableException, DataAccessException;

    Collection<Quote> get(Fund fund, Collection<LocalDate> dates) throws QuoteUnavailableException, DataAccessException;

}
