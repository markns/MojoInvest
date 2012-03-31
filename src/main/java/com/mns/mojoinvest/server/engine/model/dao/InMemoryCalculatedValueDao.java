package com.mns.mojoinvest.server.engine.model.dao;

import au.com.bytecode.opencsv.CSVReader;
import com.google.inject.Singleton;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import org.apache.commons.lang.NotImplementedException;
import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Singleton
public class InMemoryCalculatedValueDao implements CalculatedValueDao {

    private static final Logger log = Logger.getLogger(InMemoryCalculatedValueDao.class.getName());

    Map<String, CalculatedValue> calculatedValues = new HashMap<String, CalculatedValue>();

    public void init(String... filenames) {
        try {
            for (String filename : filenames) {
                log.info("Reading " + filename);
                readCVFile(filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readCVFile(String file) throws IOException {
        CSVReader reader = new CSVReader(new BufferedReader(new FileReader(file)));
        for (String[] row : reader.readAll()) {
            CalculatedValue cv = CalculatedValue.fromStrArr(row);
            if (cv != null)
                calculatedValues.put(cv.getKey(), cv);
        }
        reader.close();
    }

    @Override
    public void registerObjects(ObjectifyFactory ofyFactory) {
        throw new NotImplementedException();
    }

    @Override
    public Key<CalculatedValue> put(CalculatedValue cv) {
        throw new NotImplementedException();
    }

    @Override
    public Map<Key<CalculatedValue>, CalculatedValue> put(List<CalculatedValue> cvs) {
        throw new NotImplementedException();
    }

    @Override
    public Collection<CalculatedValue> get(List<LocalDate> dates, Collection<Fund> funds, String type, int period) {
        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
        for (LocalDate date : dates) {
            for (Fund fund : funds) {
                CalculatedValue cv = calculatedValues.get(
                        CalculatedValue.getCalculatedValueKey(date, fund.getSymbol(), type, period));
                if (cv != null)
                    cvs.add(cv);
            }
        }
        return cvs;
    }

    @Override
    public Collection<CalculatedValue> get(LocalDate date, Collection<Fund> funds, String type, int period) {
        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
        for (Fund fund : funds) {
            cvs.add(calculatedValues.get(
                    CalculatedValue.getCalculatedValueKey(date, fund.getSymbol(), type, period)));
        }
        return cvs;
    }
}
