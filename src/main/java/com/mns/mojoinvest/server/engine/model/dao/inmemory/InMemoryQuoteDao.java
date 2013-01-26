package com.mns.mojoinvest.server.engine.model.dao.inmemory;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.util.QuoteUtils;
import org.apache.commons.lang.NotImplementedException;
import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Singleton
public class InMemoryQuoteDao implements QuoteDao {

    private static final Logger log = Logger.getLogger(InMemoryQuoteDao.class.getName());


    private final Map<String, Map<LocalDate, Quote>> map = new HashMap<String, Map<LocalDate, Quote>>();

    public void init(String... filenames) {
        try {
            for (String file : filenames) {
                log.info("Reading " + file);
                readQuotesFromFile(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readQuotesFromFile(String file) throws IOException {
        CSVReader reader = new CSVReader(new BufferedReader(new FileReader(file)));
        for (String[] row : reader.readAll()) {
            if ("symbol".equals(row[0]))
                continue;
            String symbol = row[0];
            if (!map.containsKey(symbol)) {
                map.put(symbol, new HashMap<LocalDate, Quote>());
            }
            Quote quote = QuoteUtils.fromStringArray(row);
            if (quote != null)
                map.get(symbol).put(quote.getDate(), quote);
        }
        reader.close();
    }

    @Override
    public void put(Iterable<Quote> quotes) {
        throw new NotImplementedException();
    }

    @Override
    public Quote get(Fund fund, LocalDate date) {
        return map.get(fund.getSymbol()).get(date);
    }

    @Override
    public Quote get(String symbol, LocalDate date) {
        if (map.get(symbol) == null)
            return null;

        return map.get(symbol).get(date);
    }

    public List<Quote> get(Fund fund, final Collection<LocalDate> dates) {
        Map<LocalDate, Quote> quotes = map.get(fund.getSymbol());
        return new ArrayList<Quote>(Maps.filterKeys(quotes, Predicates.in(dates)).values());
    }

}


