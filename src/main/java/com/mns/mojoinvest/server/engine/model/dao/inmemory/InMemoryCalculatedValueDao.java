package com.mns.mojoinvest.server.engine.model.dao.inmemory;

import au.com.bytecode.opencsv.CSVReader;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
//            CalculatedValue cv = CalculatedValueUtils.fromStringArray(row);
//            if (cv != null)
//                calculatedValues.put(cv.getKey(), cv);
        }
        reader.close();
    }

    @Override
    public Map<String, Map<LocalDate, CalculatedValue>> get(Collection<Fund> funds, String type, int period) {
        Map<String, Map<LocalDate, CalculatedValue>> cvs = new HashMap<String, Map<LocalDate, CalculatedValue>>(funds.size());
//        for (LocalDate date : dates) {
//            for (Fund fund : funds) {
//                CalculatedValue cv = calculatedValues.get(
//                        CalculatedValue.getCalculatedValueKey(date, fund.getSymbol(), type, period));
//                if (cv != null)
//                    cvs.add(cv);
//            }
//        }
        return cvs;
    }

    @Override
    public void put(String key, List<CalculatedValue> cvs) {

    }

}
