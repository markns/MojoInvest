package com.mns.mojoinvest.server.engine.calculator;

import au.com.bytecode.opencsv.CSVReader;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.util.QuoteUtils;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.linear.BlockRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

public class CorrelationCalculator {

    private static double[][] data = {
            {3, 9, 8},
            {2, 7, 1},
            {4, 12, 4},
            {5, 15, 11},
            {6, 17, 54},
            {4, 12, 9}
    };


    public static void main(String[] args) throws IOException {


        Map<String, List<Quote>> quotes = readQuoteFiles();

        int range = 200;

        List<String> k = new ArrayList<String>(quotes.keySet());
        double data[][] = new double[range][k.size()];
        for (int j = 0; j < k.size(); j++) {

            List<Quote> q = quotes.get(k.get(j));
            QuoteUtils.sortByDateAsc(q);
            Collections.reverse(q);
            if (q.size() > range) {
                Object[] arr = q.subList(0, range).toArray();
                for (int i = 0; i < range; i++) {
                    Object o = arr[i];
                    Quote l = (Quote) o;
                    data[i][j] = l.getClose().doubleValue();
                }

            }
        }


        PearsonsCorrelation c = new PearsonsCorrelation(data);

        NumberFormat format = NumberFormat.getNumberInstance();

        System.out.println(k);
//        System.out.println(c.getCorrelationMatrix());


        XStream xstream = new XStream();
        String xml = xstream.toXML(c.getCorrelationMatrix()); // serialize to XML
        System.out.println(xml);
        Object myObject2 = xstream.fromXML(xml); // de
        RealMatrix m = (BlockRealMatrix) myObject2;
        System.out.println(m);

        System.out.print("          ");
        for (String s : k) {
            System.out.print(StringUtils.leftPad(s, 10));
        }
        System.out.println();
        for (int i = 0; i < c.getCorrelationMatrix().getColumnDimension(); i++) {
            System.out.print(StringUtils.rightPad(k.get(i), 10));
            for (int j = 0; j < c.getCorrelationMatrix().getRowDimension(); j++) {
                String s = format.format(c.getCorrelationMatrix().getEntry(i, j));
                System.out.print(StringUtils.leftPad(s, 10));

            }
            System.out.println();

        }


    }

    private static Map<String, List<Quote>> readQuoteFiles() throws IOException {
        Map<String, List<Quote>> quoteMap = new HashMap<String, List<Quote>>();

        String[] files = new String[]{"data/ishares_quotes_tr.csv", "data/ishares_missingquotes_tr.csv"};
        for (String file : files) {
            readQuotesFromFile(quoteMap, file);
        }
        return quoteMap;

    }

    private static void readQuotesFromFile(Map<String, List<Quote>> quoteMap, String file) throws IOException {
        CSVReader reader = new CSVReader(new BufferedReader(new FileReader(file)));
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
    }


}
