package com.mns.mojoinvest.server.engine.model.dao;

import au.com.bytecode.opencsv.CSVReader;
import com.google.inject.Singleton;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.*;
import org.apache.commons.lang.NotImplementedException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
public class InMemoryFundDao implements FundDao {

    Map<String, Fund> funds = new HashMap<String, Fund>();

    public void init(String... filenames) {
        try {
            for (String filename : filenames) {

                readFundFile(filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFundFile(String file) throws IOException {
        CSVReader reader = new CSVReader(new BufferedReader(new FileReader(file)));
        for (String[] row : reader.readAll()) {
            Fund fund = Fund.fromStrArr(row);
            if (fund != null)
                funds.put(fund.getSymbol(), fund);
        }
        reader.close();
    }


    @Override
    public void registerObjects(ObjectifyFactory ofyFactory) {
        throw new NotImplementedException();
    }

    @Override
    public Collection<Fund> list() {
        throw new NotImplementedException();
    }

    @Override
    public Fund get(String symbol) {
        return funds.get(symbol);
    }

    @Override
    public Collection<Fund> get(Collection<String> symbols) {
        throw new NotImplementedException();
    }

    @Override
    public Set<String> getByCategory(String categoryName) {
        throw new NotImplementedException();
    }

    @Override
    public Set<String> getByProvider(String providerName) {
        throw new NotImplementedException();
    }

    @Override
    public Map<Key<Fund>, Fund> put(Set<Fund> funds) {
        throw new NotImplementedException();
    }

    @Override
    public Key<Symbols> put(Symbols symbols) {
        throw new NotImplementedException();
    }

    @Override
    public void put(ProviderSet providerSet) {
        throw new NotImplementedException();
    }

    @Override
    public Map<Key<Provider>, Provider> putProviders(Collection<Provider> providers) {
        throw new NotImplementedException();
    }

    @Override
    public Key<CategorySet> put(CategorySet categorySet) {
        throw new NotImplementedException();
    }

    @Override
    public Map<Key<Category>, Category> putCategories(Collection<Category> values) {
        throw new NotImplementedException();
    }

    @Override
    public Set<String> getProviderSet() {
        throw new NotImplementedException();
    }

    @Override
    public Set<String> getCategorySet() {
        throw new NotImplementedException();
    }

    public Collection<Fund> getAll() {
        return funds.values();
    }
}
