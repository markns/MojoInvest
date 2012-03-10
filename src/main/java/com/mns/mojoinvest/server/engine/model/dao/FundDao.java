package com.mns.mojoinvest.server.engine.model.dao;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.*;

import java.util.*;
import java.util.logging.Logger;

public class FundDao extends DAOBase {

    private static final Logger log = Logger.getLogger(FundDao.class.getName());

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
        ofyFactory.register(Symbols.class);
        ofyFactory.register(Provider.class);
        ofyFactory.register(ProviderSet.class);
        ofyFactory.register(Category.class);
        ofyFactory.register(CategorySet.class);
        ofyFactory.getConversions().add(new MyTypeConverters());
    }


    public Collection<Fund> getAll() {
        Key<Symbols> key = new Key<Symbols>(Symbols.class, "symbols");
        Symbols symbols = ofy().get(key);

        List<Key<Fund>> keys = new ArrayList<Key<Fund>>();
        for (String symbol : symbols.getSymbols()) {
            keys.add(new Key<Fund>(Fund.class, symbol));
        }
        return ofy().get(keys).values();
    }

    public Fund get(String symbol) {
        Key<Fund> key = new Key<Fund>(Fund.class, symbol);
        return ofy().get(key);
    }

    public Collection<Fund> get(List<String> symbols) {
        List<Key<Fund>> keys = new ArrayList<Key<Fund>>();
        for (String symbol : symbols) {
            keys.add(new Key<Fund>(Fund.class, symbol));
        }
        return ofy().get(keys).values();
    }

    public Collection<Fund> getByCategory(String categoryName) {
        Key<Category> key = new Key<Category>(Category.class, categoryName);
        Category category = ofy().get(key);

        List<Key<Fund>> keys = new ArrayList<Key<Fund>>();
        for (String symbol : category.getSymbols()) {
            keys.add(new Key<Fund>(Fund.class, symbol));
        }
        return ofy().get(keys).values();
    }

    public Collection<Fund> getByProvider(String providerName) {
        Key<Provider> key = new Key<Provider>(Provider.class, providerName);
        Provider provider = ofy().get(key);

        List<Key<Fund>> keys = new ArrayList<Key<Fund>>();
        for (String symbol : provider.getSymbols()) {
            keys.add(new Key<Fund>(Fund.class, symbol));
        }
        return ofy().get(keys).values();
    }

    //*************

    public Map<Key<Fund>, Fund> put(Set<Fund> funds) {
        return ofy().put(funds);
    }

    public Key<Symbols> put(Symbols symbols) {
        return ofy().put(symbols);
    }

    public void put(ProviderSet providerSet) {
        ofy().put(providerSet);
    }

    public Map<Key<Provider>, Provider> putProviders(Collection<Provider> providers) {
        return ofy().put(providers);
    }

    public Key<CategorySet> put(CategorySet categorySet) {
        return ofy().put(categorySet);
    }

    public Map<Key<Category>, Category> putCategories(Collection<Category> values) {
        return ofy().put(values);
    }

    public Set<String> getProviderSet() {
        return ofy().get(new Key<ProviderSet>(ProviderSet.class, "providers")).getProviders();
    }

    public Set<String> getCategorySet() {
        return ofy().get(new Key<CategorySet>(CategorySet.class, "categories")).getCategories();
    }
}
