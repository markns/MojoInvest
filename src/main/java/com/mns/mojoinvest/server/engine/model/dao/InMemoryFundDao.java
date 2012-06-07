package com.mns.mojoinvest.server.engine.model.dao;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.Fund;
import org.apache.commons.lang.NotImplementedException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Singleton
public class InMemoryFundDao implements FundDao {

    private static final Logger log = Logger.getLogger(InMemoryFundDao.class.getName());

    Map<String, Fund> funds = new HashMap<String, Fund>();

    public void init(String... filenames) {
        try {
            for (String filename : filenames) {
                log.info("Reading " + filename);
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
        return funds.values();
    }

    @Override
    public Fund get(String symbol) {
        return funds.get(symbol);
    }

    @Override
    public Collection<Fund> get(Collection<String> symbols) {
        return new ArrayList<Fund>(Maps.filterKeys(funds, Predicates.in(symbols)).values());
    }

    @Override
    public Map<Key<Fund>, Fund> put(Set<Fund> funds) {
        throw new NotImplementedException();
    }


    public Collection<Fund> getAll() {
        return funds.values();
    }
}
