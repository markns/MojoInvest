package com.mns.mojoinvest.server.engine.model.dao;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.Query;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.util.FundUtils;
import com.mns.mojoinvest.server.util.QuoteUtils;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.*;

public class QuoteDaoImpl extends DAOBase implements QuoteDao {

    private static boolean objectsRegistered;

    @Inject
    public QuoteDaoImpl(final ObjectifyFactory objectifyFactory) {
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

    public Collection<Quote> get(Fund fund, Collection<LocalDate> dates) {
        List<Key<Quote>> keys = new ArrayList<Key<Quote>>();
        for (LocalDate date : dates) {
            keys.add(new Key<Quote>(Quote.class, QuoteUtils.quoteId(fund.getSymbol(), date)));
        }
        return get(keys);
    }

    public Quote get(Fund fund, LocalDate date) {
        Key<Quote> key = new Key<Quote>(Quote.class, QuoteUtils.quoteId(fund.getSymbol(), date));
        return ofy().get(key);
    }


    public Collection<Quote> getAverage(Collection<Fund> funds, LocalDate date, int averagingRange) {
        List<LocalDate> dates = TradingDayUtils.getDailySeries(date.minusDays(averagingRange), date, true);
        Collection<Quote> averageQuotes = new ArrayList<Quote>();
        Map<Fund, List<Quote>> byFund = getByFund(funds, dates);
        for (Map.Entry<Fund, List<Quote>> entry : byFund.entrySet()) {
            BigDecimal open = BigDecimal.ZERO;
            BigDecimal high = BigDecimal.ZERO;
            BigDecimal low = BigDecimal.ZERO;
            BigDecimal close = BigDecimal.ZERO;
            BigDecimal volume = BigDecimal.ZERO;
            BigDecimal adjClose = BigDecimal.ZERO;
            for (Quote quote : entry.getValue()) {
//                open = open.add(quote.getOpen());
//                high = high.add(quote.getHigh());
//                low = low.add(quote.getLow());
                close = close.add(quote.getClose());
//                volume = volume.add(quote.getVolume());
//                adjClose = adjClose.add(quote.getAdjClose());
            }
            BigDecimal size = new BigDecimal(entry.getValue().size());
//            open = open.divide(size, BigDecimal.ROUND_HALF_EVEN);
//            high = high.divide(size, BigDecimal.ROUND_HALF_EVEN);
//            low = low.divide(size, BigDecimal.ROUND_HALF_EVEN);
            close = close.divide(size, BigDecimal.ROUND_HALF_EVEN);
//            volume = volume.divide(size, BigDecimal.ROUND_HALF_EVEN);
//            adjClose = adjClose.divide(size, BigDecimal.ROUND_HALF_EVEN);
            averageQuotes.add(new Quote(entry.getKey().getSymbol(), date, open, high, low, close, null, null, volume, adjClose, true));
        }
        return averageQuotes;
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
