package com.mns.mojoinvest.server.engine.model;

import com.google.inject.Inject;
import com.googlecode.objectify.*;
import com.mns.mojoinvest.server.model.DAOBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FundDao extends DAOBase {

    private static boolean objectsRegistered;

    @Inject
    public FundDao(final ObjectifyFactory objectifyFactory) {
        super(objectifyFactory);
    }

    @Override
    protected boolean areObjectsRegistered() {
        return objectsRegistered;
    }

    @Override
    public void registerObjects(ObjectifyFactory ofyFactory) {
        objectsRegistered = true;
        ofyFactory.register(Fund.class);
        ofyFactory.getConversions().add(new MyTypeConverters());
    }

    public List<Fund> list() {
        Query<Fund> q = ofy().query(Fund.class).filter("active =", true);
        return q.list();
    }

    public List<String> getAllSymbols() {
        Query<Fund> q = ofy().query(Fund.class);
        List<String> symbols = new ArrayList<String>();
        for (Fund fund : q.list()) {
            symbols.add(fund.getSymbol());
        }
        return symbols;
    }

    public Fund get(String symbol) {
        return ofy().get(new Key<Fund>(Fund.class, symbol));
    }

    public Collection<Fund> get(List<String> symbols) {
        List<Key<Fund>> keys = new ArrayList<Key<Fund>>();
        for (String symbol : symbols) {
            keys.add(new Key<Fund>(Fund.class, symbol));
        }
        return ofy().get(keys).values();
    }

    public List<Fund> query(Map<String, Object> filters) {
        Query<Fund> q = ofy().query(Fund.class);
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            q.filter(entry.getKey(), entry.getValue());
        }
        return q.list();
    }

    public Key<Fund> put(Fund fund) {
        return ofy().put(fund);
    }

    public Map<Key<Fund>, Fund> put(Collection<Fund> funds) {
        return ofy().put(funds);
    }
}
