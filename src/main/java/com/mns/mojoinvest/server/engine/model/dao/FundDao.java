package com.mns.mojoinvest.server.engine.model.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Funds;

import java.util.*;
import java.util.logging.Logger;

@Singleton
public class FundDao extends DAOBase {

    private static final Logger log = Logger.getLogger(FundDao.class.getName());

    private static boolean objectsRegistered;

    private Map<String, Fund> funds = new HashMap<String, Fund>();
    private Map<String, Set<Fund>> providers = new HashMap<String, Set<Fund>>();
    private Map<String, Set<Fund>> categories = new HashMap<String, Set<Fund>>();

    @Inject
    public FundDao(final ObjectifyFactory objectifyFactory) {
        super(objectifyFactory);

        //Initialise local caches
        Funds funds = ofy().get(new Key<Funds>(Funds.class, "funds"));
        for (Fund fund : funds.getFunds()) {
            if (fund.isActive()) {
                this.funds.put(fund.getSymbol(), fund);
            }
            if (fund.getProvider() != null) {
                if (!providers.containsKey(fund.getProvider())) {
                    providers.put(fund.getProvider(), new HashSet<Fund>());
                }
                providers.get(fund.getProvider()).add(fund);
            }
            if (fund.getCategory() != null) {
                if (!categories.containsKey(fund.getCategory())) {
                    categories.put(fund.getCategory(), new HashSet<Fund>());
                }
                categories.get(fund.getCategory()).add(fund);
            }
        }
    }

    @Override
    protected boolean areObjectsRegistered() {
        return objectsRegistered;
    }

    @Override
    public void registerObjects(ObjectifyFactory ofyFactory) {
        objectsRegistered = true;
        ofyFactory.register(Funds.class);
        ofyFactory.getConversions().add(new MyTypeConverters());
    }

    public synchronized Key<Funds> put(Set<Fund> funds) {
        //TODO: This method should update local caches also

        return ofy().put(new Funds(funds));
    }


    public Collection<Fund> getAll() {
        return funds.values();
    }

    public Fund get(String symbol) {
        return funds.get(symbol);
    }

    public Collection<Fund> get(List<String> symbols) {
        Set<Fund> funds = new HashSet<Fund>();
        for (String symbol : symbols) {
            Fund fund = this.funds.get(symbol);
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
