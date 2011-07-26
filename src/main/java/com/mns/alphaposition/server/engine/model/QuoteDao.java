package com.mns.alphaposition.server.engine.model;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.Query;
import com.mns.alphaposition.server.model.DAOBase;
import com.mns.alphaposition.shared.engine.model.Fund;
import com.mns.alphaposition.shared.engine.model.Quote;
import com.mns.alphaposition.shared.util.FundUtils;
import com.mns.alphaposition.shared.util.QuoteUtils;
import org.joda.time.LocalDate;

import java.util.*;

public class QuoteDao extends DAOBase {

    private static boolean objectsRegistered;

    @Override
    protected boolean areObjectsRegistered() {
        return objectsRegistered;
    }

    @Override
    protected void registerObjects(ObjectifyFactory ofyFactory) {
        objectsRegistered = true;
        ofyFactory.register(Quote.class);
        ofyFactory.getConversions().add(new MyTypeConverters());
    }

    @Inject
    public QuoteDao(final ObjectifyFactory objectifyFactory) {
        super(objectifyFactory);
    }


    public Quote find(Key<Quote> key) {
        return ofy().find(key);
    }

    public Key<Quote> put(Quote quote) {
        return ofy().put(quote);
    }

    public Map<Key<Quote>, Quote> put(Iterable<Quote> quotes) {
        return ofy().put(quotes);
    }

    public List<Quote> list() {
        Query<Quote> q = ofy().query(Quote.class);
        return q.list();
    }

    public List<Quote> query(Map<String, Object> filters) {
        Query<Quote> q = ofy().query(Quote.class);
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            q.filter(entry.getKey(), entry.getValue());
        }
        return q.list();
    }

    public List<Quote> query(Fund fund) {
        return query(fund.getSymbol());
    }

    public List<Quote> query(String symbol) {
        Query<Quote> q = ofy().query(Quote.class);
        q.filter("symbol", symbol);
        return q.list();
    }

    public List<Quote> query(LocalDate date) {
        Query<Quote> q = ofy().query(Quote.class);
        q.filter("date", date.toDateMidnight().toDate());
        return q.list();
    }

    public Collection<Quote> get(List<Key<Quote>> keys) {
        return ofy().get(keys).values();
    }

    public Collection<Quote> get(Collection<Fund> funds, Collection<LocalDate> dates) {
        List<Key<Quote>> keys = new ArrayList<Key<Quote>>();
        for (LocalDate date : dates) {
            for (Fund fund : funds) {
                keys.add(new Key<Quote>(Quote.class, QuoteUtils.quoteId(fund.getSymbol(), date)));
            }
        }
        return get(keys);
    }

    public Map<Fund, List<Quote>> getByFund(Collection<Fund> funds, List<LocalDate> dates) {
        Collection<Quote> quotes = get(funds, dates);
        Map<String, Fund> symbolToFund = FundUtils.getSymbolToFundMap(funds);
        Map<Fund, List<Quote>> byFund = new HashMap<Fund, List<Quote>>();
        for (Quote quote : quotes) {
            Fund fund = symbolToFund.get(quote.getSymbol());
            if (!byFund.containsKey(fund)) {
                 byFund.put(fund, new ArrayList<Quote>());
            }
            byFund.get(fund).add(quote);
        }
//        This should be handled by a backend updating the datastore
//        for (Fund fund : byFund.keySet()) {
//            List<Quote> quotesByFund = byFund.get(fund);
//            List<Quote> missingQuotes = QuoteUtils.getMissingQuotes(fund.getInceptionDate(), new LocalDate(), quotesByFund);
//            byFund.get(fund).addAll(missingQuotes);
//        }

        return byFund;
    }


    public Map<LocalDate, List<Quote>> getByDate(Collection<Fund> funds, List<LocalDate> dates) {
        Collection<Quote> quotes = get(funds, dates);
        Map<LocalDate, List<Quote>> byDate = new HashMap<LocalDate, List<Quote>>();
        for (Quote quote : quotes) {
            LocalDate date = quote.getDate();
            if (!byDate.containsKey(date)) {
                byDate.put(date, new ArrayList<Quote>());
            }
            byDate.get(date).add(quote);
        }
        return byDate;
    }
}
