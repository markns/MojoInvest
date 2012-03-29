package com.mns.mojoinvest.server.pipeline.fund;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.*;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.ObjectifyFundDao;

import java.util.*;
import java.util.logging.Logger;

public class FundUpdaterJob extends Job1<String, List<Fund>> {

    private static final Logger log = Logger.getLogger(FundUpdaterJob.class.getName());

    @Override
    public Value<String> run(List<Fund> current) {

        //TODO: Figure out how to inject and serialize DAOs
        ObjectifyFactory factory = ObjectifyService.factory();
        FundDao dao = new ObjectifyFundDao(factory);
        dao.registerObjects(factory);
        //

        Collection<Fund> existing = null;
        try {
            existing = dao.getAll();
        } catch (NotFoundException e) {
            existing = new HashSet<Fund>(0);
        }

        //Subtract set of current funds from existing to find inactive.
        existing.removeAll(current);
        for (Fund fund : existing) {
            fund.setActive(false);
        }
        String message = "Setting " + existing.size() + " funds as inactive: " + existing;
        log.info(message);
        String returnMessage = "" + message;
        dao.put(new HashSet<Fund>(existing));

        //Build maps of categories and providers to fund symbols
        Map<String, Provider> providers = buildProviderMap(current);
        Map<String, Category> categories = buildCategoryMap(current);
        Set<String> symbols = getSymbols(current);

        message = "Updating " + current.size() + " active funds";
        log.info(message);
        returnMessage = returnMessage + "\n" + message;

        //TODO: All these saves should be wrapped in a single transaction
        dao.put(new ProviderSet(providers.keySet()));
        dao.putProviders(providers.values());
        dao.put(new CategorySet(categories.keySet()));
        dao.putCategories(categories.values());
        //TODO: No need to create hashset here, return correct collection from fund fetcher

        dao.put(new Symbols(symbols));
        dao.put(new HashSet<Fund>(current));

        return immediate(returnMessage);
    }

    private Set<String> getSymbols(List<Fund> current) {
        Set<String> symbols = new HashSet<String>(current.size());
        for (Fund fund : current) {
            symbols.add(fund.getSymbol());
        }
        return symbols;
    }

    private HashMap<String, Category> buildCategoryMap(List<Fund> current) {
        HashMap<String, Category> categories = new HashMap<String, Category>();
        for (Fund fund : current) {
            if (fund.getCategory() != null) {
                if (!categories.containsKey(fund.getCategory())) {
                    categories.put(fund.getCategory(), new Category(fund.getCategory()));
                }
                categories.get(fund.getCategory()).add(fund.getSymbol());
            }
        }
        return categories;
    }

    private HashMap<String, Provider> buildProviderMap(List<Fund> current) {
        HashMap<String, Provider> providers = new HashMap<String, Provider>();
        for (Fund fund : current) {
            if (fund.getProvider() != null) {
                if (!providers.containsKey(fund.getProvider())) {
                    providers.put(fund.getProvider(), new Provider(fund.getProvider()));
                }
                providers.get(fund.getProvider()).add(fund.getSymbol());
            }
        }
        return providers;
    }


}
