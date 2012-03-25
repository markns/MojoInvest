package com.mns.mojoinvest.server.engine.model.dao;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface QuoteDao {

    void registerObjects(ObjectifyFactory ofyFactory);

    Key<Quote> put(Quote quote);

    Map<Key<Quote>, Quote> put(Iterable<Quote> quotes);

    List<Quote> list();

    List<Quote> query(Map<String, Object> filters);

    List<Quote> query(Fund fund);

    List<Quote> query(String symbol);

    List<Quote> query(LocalDate date);

    List<Quote> query(String symbol, LocalDate date);

    Collection<Quote> get(List<Key<Quote>> keys);

    Collection<Quote> get(Collection<String> symbols, Collection<LocalDate> dates);

    List<Key<Quote>> getKeys(Collection<String> symbols, Collection<LocalDate> dates);

    List<Key<Quote>> getKeys(String symbol, Collection<LocalDate> dates);

    List<Key<Quote>> getKeys(List<String> symbols, LocalDate date);

    Collection<Quote> get(Fund fund, Collection<LocalDate> dates);

    Quote get(String symbol, LocalDate date);

    Quote get(Fund fund, LocalDate date);
}
