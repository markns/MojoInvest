package com.mns.alphaposition.server.util;

import au.com.bytecode.opencsv.CSVReader;
import com.mns.alphaposition.server.data.QuoteSet;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class DataSanity {

    public static final String DATA_DIR = "C:\\workspace\\Alpha-Position\\src\\test\\resources\\quote\\";

    @Test
    public void findFundsWithNoData() throws IOException {

        CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(QuoteSet.class.getClassLoader()
                .getResourceAsStream("etf-static.csv"))));

        for (String[] row : reader.readAll()) {
            File file = new File(DATA_DIR + row[1] + ".csv");
            if (!file.exists()) {
                System.out.println(row[1]);
            }
        }
    }


}