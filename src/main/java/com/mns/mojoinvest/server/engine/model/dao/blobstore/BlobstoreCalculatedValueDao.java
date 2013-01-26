package com.mns.mojoinvest.server.engine.model.dao.blobstore;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.common.base.Splitter;
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

        for (Fund fund : funds) {
            cvs.put(fund.getSymbol(), get(fund.getSymbol(), type, period));
        }
        return cvs;
    }

    private Map<LocalDate, CalculatedValue> get(String symbol, String type, int period)
            throws CalculatedValueUnavailableException, DataAccessException {

        String key = symbol + "|" + type + "|" + period;

        BlobstoreEntryRecord record = recordDao.get(key);
        if (record == null) {
            throw new CalculatedValueUnavailableException("Unable to find calculated values for " +
                    symbol + "  " + type + " " + period);
        }
        AppEngineFile file = fileService.getBlobFile(record.getBlobKey());

        Map<LocalDate, BigDecimal> dateCalculatedValueMap;
        try {
            dateCalculatedValueMap = readValuesFromFile(file);
            Map<LocalDate, CalculatedValue> cvMap = new HashMap<LocalDate, CalculatedValue>();
            for (Map.Entry<LocalDate, BigDecimal> e : dateCalculatedValueMap.entrySet()) {
                cvMap.put(e.getKey(), new CalculatedValue(e.getKey(), symbol, type, period, e.getValue()));
            }
            return cvMap;
        } catch (Exception e) {
            throw new DataAccessException("Unable to read values from " + file + " for " + key, e);
        }
    }


    @Override
    public void put(Iterable<CalculatedValue> cvs) throws DataAccessException {

        //Construct map of symbol|type|period -> List<CalculatedValue>
        Map<String, List<CalculatedValue>> recordKeyToCalculatedValues = new HashMap<String, List<CalculatedValue>>();
        for (CalculatedValue calculatedValue : cvs) {
            String key = calculatedValue.getKey();
            if (!recordKeyToCalculatedValues.containsKey(key)) {
                recordKeyToCalculatedValues.put(key, new ArrayList<CalculatedValue>());
            }
            recordKeyToCalculatedValues.get(key).add(calculatedValue);
        }

        //For each symbol|type|period key
        for (Map.Entry<String, List<CalculatedValue>> keyToCalculatedValues : recordKeyToCalculatedValues.entrySet()) {

            Map<LocalDate, BigDecimal> dateToValueStr = new HashMap<LocalDate, BigDecimal>();

            //Check if record (symbol|year) exists
            BlobstoreEntryRecord record = recordDao.get(keyToCalculatedValues.getKey());
            if (record != null) {
                AppEngineFile file = fileService.getBlobFile(record.getBlobKey());
                try {
                    dateToValueStr.putAll(readValuesFromFile(file));
                } catch (Exception e) {
                    throw new DataAccessException("Unable to read values from " + file + " for " +
                            keyToCalculatedValues.getKey(), e);
                }
            }
            //Add all new quotes to the dateToQuoteStrMap, overwriting as necessary
            for (CalculatedValue calculatedValue : keyToCalculatedValues.getValue()) {
                dateToValueStr.put(calculatedValue.getDate(), calculatedValue.getValue());
            }

            List<String> cvStrings = new ArrayList<String>(dateToValueStr.size());
            for (Map.Entry<LocalDate, BigDecimal> e : dateToValueStr.entrySet()) {
                cvStrings.add(e.getKey() + "|" + e.getValue());
            }

            try {
                //write all values in keyToCalculatedValues to a new AppEngineFile
                AppEngineFile file = writeValuesToBlob(keyToCalculatedValues.getKey(), cvStrings);
                //Update blobstoreEntryRecordDao - add new key, delete old one
                writeBlobstoreKeyRecord(keyToCalculatedValues.getKey(), file);
            } catch (Exception e) {
                throw new DataAccessException("Unable to write calculated values to blobstore", e);
            }

            if (record != null) {
                //delete old blob - remember it's not possible to update
                blobService.delete(record.getBlobKey());
            }
        }
    }


    private static final Splitter SPLITTER = Splitter.on('|')
            .trimResults()
            .omitEmptyStrings();

    private Map<LocalDate, BigDecimal> readValuesFromFile(AppEngineFile file) throws IOException {
        // Later, read from the file using the file API
        boolean lock = false; // Let other people read at the same time
        FileReadChannel readChannel = fileService.openReadChannel(file, lock);

        // Again, different standard Java ways of reading from the channel.
        BufferedReader reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));

        Map<LocalDate, BigDecimal> dateValueMap = new HashMap<LocalDate, BigDecimal>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty())
                continue;
            Iterator<String> s = SPLITTER.split(line).iterator();

            LocalDate date = new LocalDate(s.next());
            BigDecimal value = new BigDecimal(s.next());
            dateValueMap.put(date, value);
        }


        readChannel.close();
        return dateValueMap;
    }


}
