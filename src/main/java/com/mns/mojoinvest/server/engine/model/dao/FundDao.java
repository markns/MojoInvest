package com.mns.mojoinvest.server.engine.model.dao;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Funds;

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
        ofyFactory.register(Funds.class);
        ofyFactory.getConversions().add(new MyTypeConverters());
    }

    public Key<Funds> put(Set<Fund> funds) {
        //This method should update local caches also
        return ofy().put(new Funds(funds));
    }

    private Map<String, Fund> activeFunds;
    private Map<String, Set<Fund>> providers;
    private Map<String, Set<Fund>> categories;

    public Collection<Fund> getAll() {

        if (activeFunds == null || providers == null || categories == null) {
            activeFunds = new HashMap<String, Fund>();
            providers = new HashMap<String, Set<Fund>>();
            categories = new HashMap<String, Set<Fund>>();
            Funds funds = ofy().get(new Key<Funds>(Funds.class, "funds"));
            for (Fund fund : funds.getFunds()) {
                if (fund.isActive()) {
                    activeFunds.put(fund.getSymbol(), fund);
                }
                if (!providers.containsKey(fund.getProvider())) {
                    providers.put(fund.getProvider(), new HashSet<Fund>());
                }
                providers.get(fund.getProvider()).add(fund);

                if (!categories.containsKey(fund.getCategory())) {
                    categories.put(fund.getCategory(), new HashSet<Fund>());
                }
                categories.get(fund.getCategory()).add(fund);
            }
        }
        return activeFunds.values();

    }

    public Fund get(String symbol) {
        return activeFunds.get(symbol);
    }

    public Collection<Fund> get(List<String> symbols) {
        Set<Fund> funds = new HashSet<Fund>();
        for (String symbol : symbols) {
            Fund fund = activeFunds.get(symbol);
            if (fund != null) {
                funds.add(fund);
            }
        }
        return funds;
    }

    public List<String> getProviders() {
        return new ArrayList<String>(providers.keySet());
    }

    public List<String> getCategories() {
        return new ArrayList<String>(categories.keySet());
    }

    public Collection<? extends Fund> getByCategory(String category) {
        return categories.get(category);
    }

    public Collection<? extends Fund> getByProvider(String provider) {
        return providers.get(provider);
    }
}
