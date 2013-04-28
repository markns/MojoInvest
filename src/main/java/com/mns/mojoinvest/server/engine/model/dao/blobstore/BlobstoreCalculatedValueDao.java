package com.mns.mojoinvest.server.engine.model.dao.blobstore;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.BlobstoreEntryRecord;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueUnavailableException;
import com.mns.mojoinvest.server.engine.model.dao.DataAccessException;
import com.mns.mojoinvest.server.engine.model.dao.objectify.ObjectifyEntryRecordDao;
import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.Channels;
import java.util.*;
import java.util.logging.Logger;

public class BlobstoreCalculatedValueDao extends BlobstoreDao implements CalculatedValueDao {

    private static final Logger log = Logger.getLogger(BlobstoreCalculatedValueDao.class.getName());

    @Inject
    public BlobstoreCalculatedValueDao(ObjectifyEntryRecordDao recordDao) {
        super(recordDao);
    }

    @Override
    public Map<String, Map<LocalDate, CalculatedValue>> get(Collection<Fund> funds, String type, int period) {
        Map<String, Map<LocalDate, CalculatedValue>> cvs = new HashMap<String, Map<LocalDate, CalculatedValue>>(funds.size());

        Map<String, List<String>> symbolCV = get(type, period);

        for (Fund fund : funds) {
            List<String> cvStrings = symbolCV.get(fund.getSymbol());
            Map<LocalDate, CalculatedValue> cvMap = new HashMap<LocalDate, CalculatedValue>();
            String[] arr;
            for (String cvString : cvStrings) {
                arr = cvString.split("\\|");
                LocalDate date = new LocalDate(arr[0]);
                cvMap.put(date, new CalculatedValue(date, fund.getSymbol(), type, period, new BigDecimal(arr[1])));
            }
            cvs.put(fund.getSymbol(), cvMap);
        }
        return cvs;
    }

    private Map<String, List<String>> get(String type, int period)
            throws CalculatedValueUnavailableException, DataAccessException {

        String key = type + "|" + period;

        BlobstoreEntryRecord record = recordDao.get(key);
        if (record == null) {
            throw new CalculatedValueUnavailableException("Unable to find calculated values for " +
                    type + " " + period);
        }
        AppEngineFile file = fileService.getBlobFile(record.getBlobKey());

        try {
            return readValuesFromFile(file);
        } catch (Exception e) {
            throw new DataAccessException("Unable to read values from " + file + " for " + key, e);
        }
    }


    @Override
    public void put(String key, List<CalculatedValue> cvs) {

        //Check if record (symbol|year) exists
        BlobstoreEntryRecord record = recordDao.get(key);
        if (record != null) {
            AppEngineFile file = fileService.getBlobFile(record.getBlobKey());
            try {
                fileService.delete(file);
            } catch (IOException e) {
                throw new DataAccessException("Unable to remove calculated values for " + key + " from blobstore", e);
            }
        }
        List<String> cvStrings = new ArrayList<String>(cvs.size());
        for (CalculatedValue cv : cvs) {
            cvStrings.add(cv.getSymbol() + "|" + cv.getDate() + "|" + cv.getValue());
        }

        try {
            //write all values in keyToCalculatedValues to a new AppEngineFile
            AppEngineFile file = writeValuesToBlob(key, cvStrings);
            //Update blobstoreEntryRecordDao - add new key, delete old one
            writeBlobstoreKeyRecord(key, file);
        } catch (Exception e) {
            throw new DataAccessException("Unable to write calculated values for " + key + " to blobstore", e);
        }


    }

    private Map<String, List<String>> readValuesFromFile(AppEngineFile file) throws IOException {
        // Later, read from the file using the file API
        boolean lock = false; // Let other people read at the same time
        FileReadChannel readChannel = fileService.openReadChannel(file, lock);

        // Again, different standard Java ways of reading from the channel.
        BufferedReader reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));

        Map<String, List<String>> symbolCV = new HashMap<String, List<String>>();
        String line;
        String[] arr;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty())
                continue;

            arr = line.split("\\|", 2);
            if (!symbolCV.containsKey(arr[0])) {
                symbolCV.put(arr[0], new ArrayList<String>());
            }
            symbolCV.get(arr[0]).add(arr[1]);
        }

        readChannel.close();
        return symbolCV;
    }


}
