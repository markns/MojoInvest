package com.mns.mojoinvest.server.engine.model.dao;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.util.QuoteUtils;
import org.apache.commons.lang.NotImplementedException;
import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InMemoryQuoteDao implements QuoteDao {

    private final Map<String, List<Quote>> map = new HashMap<String, List<Quote>>();

    public InMemoryQuoteDao(List<String> filenames) {
        try {
            readQuoteFiles(filenames);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readQuoteFiles(List<String> filenames) throws IOException {
        for (String file : filenames) {
            readQuotesFromFile(file);
        }
        System.out.println("finished reading quotes");

    }

    private void readQuotesFromFile(String file) throws IOException {
        CSVReader reader = new CSVReader(new BufferedReader(new FileReader(file)));
        for (String[] row : reader.readAll()) {
            if ("symbol".equals(row[0]))
                continue;
            String symbol = row[0];
            if (!map.containsKey(symbol)) {
                map.put(symbol, new ArrayList<Quote>());
            }
            Quote quote = QuoteUtils.fromStringArray(row);
            map.get(symbol).add(quote);
        }
        reader.close();
    }

    public List<Quote> get(Fund fund, final Collection<LocalDate> dates) {
        List<Quote> quotes = map.get(fund.getSymbol());
        return Lists.newArrayList(Iterables.filter(quotes, new Predicate<Quote>() {
            @Override
            public boolean apply(Quote input) {
                return dates.contains(input.getDate());
            }
        }));
    }

    @Override
    public void registerObjects(ObjectifyFactory ofyFactory) {
        throw new NotImplementedException();
    }

    @Override
    public Key<Quote> put(Quote quote) {
        throw new NotImplementedException();
    }

    @Override
    public Map<Key<Quote>, Quote> put(Iterable<Quote> quotes) {
        throw new NotImplementedException();
    }

    @Override
    public List<Quote> list() {
        throw new NotImplementedException();
    }

    @Override
    public List<Quote> query(Map<String, Object> filters) {
        throw new NotImplementedException();
    }

    @Override
    public List<Quote> query(Fund fund) {
        return map.get(fund.getSymbol());
    }

    @Override
    public List<Quote> query(String symbol) {
        return map.get(symbol);
    }

    @Override
    public List<Quote> query(LocalDate date) {
        throw new NotImplementedException();
    }

    @Override
    public List<Quote> query(String symbol, LocalDate date) {
        throw new NotImplementedException();
    }

    @Override
    public Collection<Quote> get(List<Key<Quote>> keys) {
        throw new NotImplementedException();
    }

    @Override
    public Collection<Quote> get(Collection<String> symbols, Collection<LocalDate> dates) {
        throw new NotImplementedException();
    }

    @Override
    public List<Key<Quote>> getKeys(Collection<String> symbols, Collection<LocalDate> dates) {
        throw new NotImplementedException();
    }

    @Override
    public List<Key<Quote>> getKeys(String symbol, Collection<LocalDate> dates) {
        throw new NotImplementedException();
    }

    @Override
    public List<Key<Quote>> getKeys(List<String> symbols, LocalDate date) {
        throw new NotImplementedException();
    }

    @Override
    public Quote get(String symbol, LocalDate date) {
        throw new NotImplementedException();
    }

    @Override
    public Quote get(Fund fund, LocalDate date) {
        throw new NotImplementedException();
    }
}


