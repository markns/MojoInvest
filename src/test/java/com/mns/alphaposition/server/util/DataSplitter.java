package com.mns.alphaposition.server.util;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.mns.alphaposition.server.data.QuoteSet;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DataSplitter {

    public static final String DATA_DIR = "C:\\workspace\\Alpha-Position\\data\\etf\\";

    public static void main(String[] args) {

        CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(QuoteSet.class.getClassLoader()
                .getResourceAsStream("etf-historical-data-all.csv"))));

        Map<String, CSVWriter> writers = new HashMap<String, CSVWriter>();
        try {
            String[] row;
            while ((row = reader.readNext()) != null) {
                if (!writers.containsKey(row[0])) {
                    File textFile = new File(DATA_DIR + row[0] + ".csv");
                    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(textFile));
                    writers.put(row[0], new CSVWriter(writer));
                }
                writers.get(row[0]).writeNext(row);
            }
            for (CSVWriter csvWriter : writers.values()) {
                csvWriter.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


}
