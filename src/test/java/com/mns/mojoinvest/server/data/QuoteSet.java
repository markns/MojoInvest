package com.mns.mojoinvest.server.data;

import au.com.bytecode.opencsv.CSVReader;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.util.QuoteUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuoteSet {

    public static List<Quote> getQuotesByProvider(List<String> providers) {
        CSVReader fundCsv = new CSVReader(new BufferedReader(new InputStreamReader(QuoteSet.class.getClassLoader()
                .getResourceAsStream("data/etf-static.csv"))));
//                        .getResourceAsStream("quote/ishares/ishares.csv"))));
        List<Quote> quotes = new ArrayList<Quote>();
        try {
            for (String[] fundRow : fundCsv.readAll()) {
                String property = fundRow[3];
                if (providers.contains(property)) {
                    InputStream is = QuoteSet.class.getClassLoader().
                            getResourceAsStream("data/quote/" + fundRow[1] + ".csv");
//                            getResourceAsStream("quote/ishares/" + fundRow[1] + ".csv");
                    if (is != null) {
                        CSVReader quoteCsv = new CSVReader(new BufferedReader(new InputStreamReader(is)));
                        for (String[] quoteRow : quoteCsv.readAll()) {
                            quotes.add(QuoteUtils.fromStringArray(quoteRow));
                        }
                        quoteCsv.close();
                    }
                }
            }
            fundCsv.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return quotes;
    }




    @Test
    public void testGetQuotesByProvider() {
        List<Quote> quotes = getQuotesByProvider(Arrays.asList("Van Eck"));
    }

}
