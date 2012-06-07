package com.mns.mojoinvest.server.engine.model.dao;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.*;

import java.util.*;
import java.util.logging.Logger;

public class ObjectifyFundDao extends DAOBase implements FundDao {

    private static final Logger log = Logger.getLogger(ObjectifyFundDao.class.getName());

    private static boolean objectsRegistered;

    @Inject
    public ObjectifyFundDao(final ObjectifyFactory objectifyFactory) {
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
        ofyFactory.register(Symbols.class);
        ofyFactory.register(Provider.class);
        ofyFactory.register(ProviderSet.class);
        ofyFactory.register(Category.class);
        ofyFactory.register(CategorySet.class);
        ofyFactory.getConversions().add(new MyTypeConverters());
    }

    @Override
    public Collection<Fund> list() {
        return ofy().query(Fund.class).list();
    }

    @Override
    public Collection<Fund> getAll() {
        Key<Symbols> key = new Key<Symbols>(Symbols.class, "symbols");
        Symbols symbols = ofy().get(key);

        List<Key<Fund>> keys = new ArrayList<Key<Fund>>();
        for (String symbol : symbols.getSymbols()) {
            keys.add(new Key<Fund>(Fund.class, symbol));
        }
        return ofy().get(keys).values();
    }

    @Override
    public Fund get(String symbol) {
        Key<Fund> key = new Key<Fund>(Fund.class, symbol);
        return ofy().get(key);
    }

    @Override
    public Collection<Fund> get(Collection<String> symbols) {
        List<Key<Fund>> keys = new ArrayList<Key<Fund>>();
        for (String symbol : symbols) {
            keys.add(new Key<Fund>(Fund.class, symbol));
        }
        return ofy().get(keys).values();
    }

    @Override
    public Map<Key<Fund>, Fund> put(Set<Fund> funds) {
        return ofy().put(funds);
    }
}
