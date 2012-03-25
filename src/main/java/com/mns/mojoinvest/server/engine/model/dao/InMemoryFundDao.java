package com.mns.mojoinvest.server.engine.model.dao;

import au.com.bytecode.opencsv.CSVReader;
import com.mns.mojoinvest.server.engine.model.Fund;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InMemoryFundDao {

    List<Fund> funds = new ArrayList<Fund>();

    public InMemoryFundDao(String... filenames) {
        try {
            for (String filename : filenames) {

                readFundFile(filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFundFile(String file) throws IOException {
        CSVReader reader = new CSVReader(new BufferedReader(new FileReader(file)));
        for (String[] row : reader.readAll()) {
            Fund fund = Fund.fromStrArr(row);
            funds.add(fund);
        }
        reader.close();
    }


    public Collection<Fund> getAll() {
        return funds;
    }
}
