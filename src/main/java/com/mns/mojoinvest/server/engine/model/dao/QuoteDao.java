package com.mns.mojoinvest.server.engine.model.dao;

import com.googlecode.objectify.Key;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface QuoteDao {

    Quote find(Key<Quote> key);

    Key<Quote> put(Quote quote);

    Map<Key<Quote>, Quote> put(Iterable<Quote> quotes);

    List<Quote> list();

    List<Quote> query(Map<String, Object> filters);

    List<Quote> query(Fund fund);

    List<Quote> query(String symbol);

    List<Quote> query(LocalDate date);

    Collection<Quote> get(List<Key<Quote>> keys);

    Collection<Quote> get(Collection<Fund> funds, Collection<LocalDate> dates);

    Collection<Quote> get(Fund fund, Collection<LocalDate> dates);

    Quote get(Fund fund, LocalDate date);

    Collection<Quote> getAverage(Collection<Fund> funds, LocalDate date, int averagingRange);

    Map<Fund, List<Quote>> getByFund(Collection<Fund> funds, List<LocalDate> dates);

    Map<LocalDate, List<Quote>> getByDate(Collection<Fund> funds, List<LocalDate> dates);

}
