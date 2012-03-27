package com.mns.mojoinvest.server.tools;

import au.com.bytecode.opencsv.CSVReader;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.util.QuoteUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindEarliestQuoteTool {

    public static void main(String[] args) throws IOException {
        FindEarliestQuoteTool tool = new FindEarliestQuoteTool();
        tool.run();
    }

    private void run() throws IOException {

        String[] files = new String[]{"data/etf_asset_alloc_quotes.csv"};


        Map<String, List<Quote>> quoteMap = new HashMap<String, List<Quote>>();
        for (String file : files) {

            CSVReader reader = new CSVReader(new BufferedReader(new FileReader(file)));

            String[] row;
            while ((row = reader.readNext()) != null) {
                if ("symbol".equals(row[0]))
                    continue;
                Quote quote = QuoteUtils.fromStringArray(row);
                if (!quoteMap.containsKey(quote.getSymbol()))
                    quoteMap.put(quote.getSymbol(), new ArrayList<Quote>());
                quoteMap.get(quote.getSymbol()).add(quote);
            }
            reader.close();
        }
        for (List<Quote> quotes : quoteMap.values()) {

            QuoteUtils.sortByDateAsc(quotes);
            System.out.println(quotes.get(0).getSymbol() + "," + quotes.get(0).getDate());
        }


    }

}
