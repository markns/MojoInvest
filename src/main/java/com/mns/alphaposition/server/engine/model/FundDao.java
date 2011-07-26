package com.mns.alphaposition.server.engine.model;

import com.google.inject.Inject;
import com.googlecode.objectify.*;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.alphaposition.server.model.DAOBase;
import com.mns.alphaposition.shared.ObjectAlreadyCreatedException;
import com.mns.alphaposition.shared.TransactionFailedException;
import com.mns.alphaposition.shared.engine.model.Fund;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FundDao extends DAOBase {

    private static boolean objectsRegistered;

    @Override
    protected boolean areObjectsRegistered() {
        return objectsRegistered;
    }

    @Override
    protected void registerObjects(ObjectifyFactory ofyFactory) {
        objectsRegistered = true;
        ofyFactory.register(Fund.class);
        ofyFactory.getConversions().add(new MyTypeConverters());
    }

    @Inject
    public FundDao(final ObjectifyFactory objectifyFactory) {
        super(objectifyFactory);
    }

    public Collection<Fund> getAll() {
        Query<Fund> q = ofy().query(Fund.class);
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

}
