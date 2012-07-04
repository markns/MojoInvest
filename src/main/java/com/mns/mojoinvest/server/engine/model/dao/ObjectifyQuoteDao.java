package com.mns.mojoinvest.server.engine.model.dao;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.Query;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.util.QuoteUtils;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ObjectifyQuoteDao extends DAOBase implements QuoteDao {

    private static final Logger log = Logger.getLogger(ObjectifyQuoteDao.class.getName());

    private static boolean objectsRegistered;

    @Inject
    public ObjectifyQuoteDao(final ObjectifyFactory objectifyFactory) {
        super(objectifyFactory);
    }

    @Override
    protected boolean areObjectsRegistered() {
        return objectsRegistered;
    }

    @Override
    public void registerObjects(ObjectifyFactory ofyFactory) {
        objectsRegistered = true;
        ofyFactory.register(Quote.class);
        ofyFactory.getConversions().add(new MyTypeConverters());
    }

    @Override
    public Key<Quote> put(Quote quote) {
        return ofy().put(quote);
    }

    @Override
    public Map<Key<Quote>, Quote> put(Iterable<Quote> quotes) {
        return ofy().put(quotes);
    }

    @Override
    public List<Quote> query(String symbol) {
        Query<Quote> q = ofy().query(Quote.class);
        q.filter("symbol", symbol);
        return q.list();
    }

    @Override
    public List<Quote> query(LocalDate date) {
        Query<Quote> q = ofy().query(Quote.class);
        q.filter("date", date.toDate());
        return q.list();
    }

    @Override
    public Quote get(String symbol, LocalDate date) {
        Key<Quote> key = new Key<Quote>(Quote.class, QuoteUtils.quoteId(symbol, date));
        return ofy().get(key);
    }

    @Override
    public Quote get(Fund fund, LocalDate date) {
        return get(fund.getSymbol(), date);
    }

    private Collection<Quote> get(List<Key<Quote>> keys) {
        return ofy().get(keys).values();
    }

    @Override
    public Collection<Quote> get(Collection<String> symbols, Collection<LocalDate> dates) {
        List<Key<Quote>> keys = getKeys(symbols, dates);
        return get(keys);
    }

    @Override
    public Collection<Quote> get(Fund fund, Collection<LocalDate> dates) {
        List<Key<Quote>> keys = new ArrayList<Key<Quote>>();
        for (LocalDate date : dates) {
            keys.add(new Key<Quote>(Quote.class, QuoteUtils.quoteId(fund.getSymbol(), date)));
        }
        return get(keys);
    }

    private List<Key<Quote>> getKeys(Collection<String> symbols, Collection<LocalDate> dates) {
        List<Key<Quote>> keys = new ArrayList<Key<Quote>>();
        for (LocalDate date : dates) {
            for (String symbol : symbols) {
                keys.add(new Key<Quote>(Quote.class, QuoteUtils.quoteId(symbol, date)));
            }
        }
        return keys;
    }


}
