package com.mns.alphaposition.server.data;

import au.com.bytecode.opencsv.CSVReader;
import com.mns.alphaposition.server.engine.model.Quote;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuoteSet {

    public static List<Quote> getQuotesByProvider(List<String> providers) {
        CSVReader fundCsv = new CSVReader(new BufferedReader(new InputStreamReader(QuoteSet.class.getClassLoader()
                .getResourceAsStream("etf-static.csv"))));
//                        .getResourceAsStream("quote/ishares/ishares.csv"))));
        List<Quote> quotes = new ArrayList<Quote>();
        try {
            for (String[] fundRow : fundCsv.readAll()) {
                String property = fundRow[3];
                if (providers.contains(property)) {
                    InputStream is = QuoteSet.class.getClassLoader().
                            getResourceAsStream("quote/" + fundRow[1] + ".csv");
//                            getResourceAsStream("quote/ishares/" + fundRow[1] + ".csv");
                    if (is != null) {
                        CSVReader quoteCsv = new CSVReader(new BufferedReader(new InputStreamReader(is)));
                        for (String[] quoteRow : quoteCsv.readAll()) {
                            quotes.add(createQuote(quoteRow));
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

    private static Quote createQuote(String[] row) {
        return new Quote(row[0],
                new LocalDate(row[1]),
                row[2].isEmpty() ? null : new BigDecimal(row[2]),
                row[3].isEmpty() ? null : new BigDecimal(row[3]),
                row[4].isEmpty() ? null : new BigDecimal(row[4]),
                row[5].isEmpty() ? null : new BigDecimal(row[5]),
                row[6].isEmpty() ? null : new BigDecimal(row[6]),
                row[7].isEmpty() ? null : new BigDecimal(row[7]),
                false);
    }


    @Test
    public void testGetQuotesByProvider() {
        List<Quote> quotes = getQuotesByProvider(Arrays.asList("Van Eck"));
    }

}
