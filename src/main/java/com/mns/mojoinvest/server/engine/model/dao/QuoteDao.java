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

    Quote get(String symbol, LocalDate date);

    Quote get(Fund fund, LocalDate date);

    Collection<Quote> get(Fund fund, Collection<LocalDate> dates);

    Collection<Quote> get(List<Key<Quote>> keys);

    Collection<Quote> get(Collection<String> symbols, Collection<LocalDate> dates);

    //Query methods only used by QuoteViewerServlet
    List<Quote> query(String symbol);

    List<Quote> query(LocalDate date);
}
