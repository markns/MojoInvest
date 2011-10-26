package com.mns.mojoinvest.server.util;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.mns.mojoinvest.server.data.QuoteSet;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class DataSanity {

    public static final String DATA_DIR = "C:\\workspace\\Alpha-Position\\src\\test\\resources\\quote\\";

    // Run and pipe to missing.txt
    //grep -f missing.txt -v etf-static.csv  > etf-static2.csv

    @Test
    public void findFundsWithNoData() throws IOException {
        CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(QuoteSet.class.getClassLoader()
                .getResourceAsStream("data/etf-static.csv"))));
        for (String[] row : reader.readAll()) {
            File file = new File(DATA_DIR + row[1] + ".csv");
            if (!file.exists()) {
                System.out.println(row[1]);
            }
        }
    }

//    name,symbol,category,provider,aum,expenseRatio,inceptionDate,averageVol
    @Test
    public void fundStatistics() throws IOException {
        CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(QuoteSet.class.getClassLoader()
                .getResourceAsStream("data/etf-static.csv"))));
        Map<String, Integer> categories = new HashMap<String, Integer>();
        Map<String, Integer> providers = new HashMap<String, Integer>();
        for (String[] row : reader.readAll()) {
            int count = categories.containsKey(row[2]) ? categories.get(row[2]) : 0;
            categories.put(row[2], count + 1);
            count = providers.containsKey(row[3]) ? providers.get(row[3]) : 0;
            providers.put(row[3], count + 1);
        }

        printSortedByValue(categories);
        System.out.println();
        printSortedByValue(providers);
    }

    private void printSortedByValue(Map<String, Integer> propertyMap) {
        Ordering<String> valueComparator = Ordering.natural().onResultOf(Functions.forMap(propertyMap)).
                reverse().compound(Ordering.natural());;
        SortedMap<String, Integer> map = ImmutableSortedMap.copyOf(propertyMap, valueComparator);

        for (Map.Entry<String, Integer> e : map.entrySet()) {
            System.out.println(e.getKey() + " " + e.getValue());
        }
    }

}