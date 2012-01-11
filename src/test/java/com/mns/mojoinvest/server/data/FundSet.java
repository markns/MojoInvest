package com.mns.mojoinvest.server.data;

import au.com.bytecode.opencsv.CSVReader;
import com.mns.mojoinvest.server.engine.model.Fund;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FundSet {

    public static List<Fund> getFundsByProvider(List<String> providers) {
        List<Fund> funds = new ArrayList<Fund>();
        for (String provider : providers) {
            funds.addAll(getFundInternal(3, provider));
        }
        return funds;
    }

    public static List<Fund> getFundsByCategory(List<String> categories) {
        List<Fund> funds = new ArrayList<Fund>();
        for (String category : categories) {
            funds.addAll(getFundInternal(2, category));
        }
        return funds;
    }

    private static List<Fund> getFundInternal(int index, String property) {
        CSVReader reader = null;
        try {
            reader = new CSVReader(new BufferedReader(new FileReader("C:\\workspace\\Alpha-Position\\data\\etf-static.csv")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        .getResourceAsStream("quote/ishares/ishares.csv"))));
        List<Fund> funds = new ArrayList<Fund>();
        try {
            for (String[] row : reader.readAll()) {
                if (row[index].equals(property))
                    funds.add(createFund(row));
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return funds;
    }

    private static Fund createFund(String[] row) {
        return new Fund(row[1], row[0], row[2], row[3], blankCheckBigDecimal(row[4]), blankCheckBigDecimal(row[5]),
                blankCheckLocalDate(row[6]), blankCheckBigDecimal(row[7]));
    }

    private static LocalDate blankCheckLocalDate(String str) {
        if (str.isEmpty())
            return null;
        return new LocalDate(str);
    }

    private static BigDecimal blankCheckBigDecimal(String str) {
        if (str.isEmpty())
            return null;
        return new BigDecimal(str);
    }

    @Ignore
    @Test
    public void testGetFundsByProvider() {
        List<Fund> quotes = getFundsByProvider(Arrays.asList("iShares"));
    }

    @Ignore
    @Test
    public void testGetFundsByCategory() {
        List<Fund> quotes = getFundsByCategory(Arrays.asList("Equity Energy", "Financial"));
    }
}
