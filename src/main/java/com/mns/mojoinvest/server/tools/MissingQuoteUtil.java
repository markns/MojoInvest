package com.mns.mojoinvest.server.tools;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.util.QuoteUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissingQuoteUtil {

    public static void main(String[] args) {

        try {
            Map<String, List<Quote>> quoteMap = readQuotes();
            List<Quote> missing = getMissingQuotes(quoteMap);
            writeMissingQuotes(missing);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Map<String, List<Quote>> readQuotes() throws IOException {
        CSVReader reader = new CSVReader(new BufferedReader(new FileReader("data/ishares_quotes_tr.csv")));

        Map<String, List<Quote>> quoteMap = new HashMap<String, List<Quote>>();

        for (String[] row : reader.readAll()) {
            if ("symbol".equals(row[0]))
                continue;
            String symbol = row[0];
            if (!quoteMap.containsKey(symbol)) {
                quoteMap.put(symbol, new ArrayList<Quote>());
            }
            quoteMap.get(symbol).add(QuoteUtils.fromStringArray(row));
        }
        reader.close();
        return quoteMap;
    }

    private static void writeMissingQuotes(List<Quote> missing) throws IOException {
        CSVWriter writer = new CSVWriter(new BufferedWriter(new FileWriter("data/ishares_quotes_tr_missing.csv")));
        for (Quote quote : missing) {
            writer.writeNext(QuoteUtils.toStringArray(quote));
        }
        writer.close();
    }

    private static List<Quote> getMissingQuotes(Map<String, List<Quote>> quoteMap) {
        List<Quote> missing = new ArrayList<Quote>();
        for (String symbol : quoteMap.keySet()) {
            missing.addAll(QuoteUtils.rollMissingQuotes(quoteMap.get(symbol)));
        }
        return missing;
    }


}

