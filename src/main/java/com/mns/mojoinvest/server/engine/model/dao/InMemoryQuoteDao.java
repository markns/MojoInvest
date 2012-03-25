package com.mns.mojoinvest.server.engine.model.dao;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.util.QuoteUtils;
import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryQuoteDao {

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

    public List<Quote> get(Fund fund) {
        return map.get(fund.getSymbol());
    }

    public Iterable<Quote> get(Fund fund, final List<LocalDate> dates) {
        List<Quote> quotes = map.get(fund.getSymbol());
        return Iterables.filter(quotes, new Predicate<Quote>() {
            @Override
            public boolean apply(Quote input) {
                return dates.contains(input.getDate());
            }
        });
    }

}


