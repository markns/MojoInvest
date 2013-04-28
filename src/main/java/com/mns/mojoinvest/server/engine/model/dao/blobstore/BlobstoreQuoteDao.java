package com.mns.mojoinvest.server.engine.model.dao.blobstore;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.BlobstoreEntryRecord;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.DataAccessException;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteUnavailableException;
import com.mns.mojoinvest.server.engine.model.dao.objectify.ObjectifyEntryRecordDao;
import com.mns.mojoinvest.server.util.QuoteUtils;
import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.nio.channels.Channels;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlobstoreQuoteDao extends BlobstoreDao implements QuoteDao {

    private static final Logger log = Logger.getLogger(BlobstoreQuoteDao.class.getName());

    @Inject
    public BlobstoreQuoteDao(ObjectifyEntryRecordDao recordDao) {
        super(recordDao);
    }

    @Override
    public synchronized void put(Iterable<Quote> quotes) throws DataAccessException {

        //Construct map of symbol|year -> List<Quote>
        Map<String, List<Quote>> recordKeyToQuotes = new HashMap<String, List<Quote>>();
        for (Quote quote : quotes) {
            String key = quote.getSymbol() + "|" + quote.getDate().getYear();
            if (!recordKeyToQuotes.containsKey(key)) {
                recordKeyToQuotes.put(key, new ArrayList<Quote>());
            }
            recordKeyToQuotes.get(key).add(quote);
        }

        //For each symbol|year key
        for (Map.Entry<String, List<Quote>> keyToQuotes : recordKeyToQuotes.entrySet()) {

            Map<String, String> dateToQuoteStr = new HashMap<String, String>();

            //Check if record (symbol|year) exists
            BlobstoreEntryRecord record = recordDao.get(keyToQuotes.getKey());
            if (record != null) {
                AppEngineFile file = fileService.getBlobFile(record.getBlobKey());
                try {
                    dateToQuoteStr.putAll(readValuesFromFile(file));
                } catch (Exception e) {
                    throw new DataAccessException("Unable to read values from " + file + " for " +
                            keyToQuotes.getKey(), e);
                }
            }
            //Add all new quotes to the dateToQuoteStrMap, overwriting as necessary
            for (Quote quote : keyToQuotes.getValue()) {
                dateToQuoteStr.put(quote.getDate().toString(), QuoteUtils.toString(quote));
            }

            try {
                //write all values in keyToQuotes to a new AppEngineFile
                AppEngineFile file = writeValuesToBlob(keyToQuotes.getKey(), dateToQuoteStr.values());
                //Update blobstoreEntryRecordDao - add new key, delete old one
                writeBlobstoreKeyRecord(keyToQuotes.getKey(), file);
            } catch (Exception e) {
                throw new DataAccessException("Unable to write quotes to blobstore", e);
            }

            if (record != null) {
                //delete old blob - remember it's not possible to update
                blobService.delete(record.getBlobKey());
            }
        }
    }

    //TODO: replace with memCache
    Map<String, Map<String, String>> quoteCache = new HashMap<String, Map<String, String>>();

    @Override
    public Quote get(String symbol, LocalDate date) throws QuoteUnavailableException, DataAccessException {

        if (!quoteCache.containsKey(symbol) || !quoteCache.get(symbol).containsKey(date.toString())) {
//        log.info(Thread.currentThread().getName() + " enter get " + symbol + " " + date);
            BlobstoreEntryRecord record = recordDao.get(symbol + "|" + date.getYear());
            if (record == null)
                throw new QuoteUnavailableException("Unable to find quote for " + symbol + " on " + date);
            AppEngineFile file = fileService.getBlobFile(record.getBlobKey());

            HashMap<String, String> dateStrQuoteStrMap;
            try {
                dateStrQuoteStrMap = readValuesFromFile(file);
            } catch (Exception e) {
                throw new DataAccessException("Unable to read values from " + file +
                        " for " + symbol + " on " + date, e);
            }
            if (!quoteCache.containsKey(symbol)) {
                quoteCache.put(symbol, new HashMap<String, String>());
            }
            quoteCache.get(symbol).putAll(dateStrQuoteStrMap);
//            log.info(Thread.currentThread().getName() + " exit get");
        }

        String quoteStr = quoteCache.get(symbol).get(date.toString());

        if (quoteStr == null || quoteStr.isEmpty())
            throw new QuoteUnavailableException("Unable to find quote for " + symbol + " on " + date);
        return QuoteUtils.fromString(quoteStr);
    }


    @Override
    public Quote get(Fund fund, LocalDate date) throws QuoteUnavailableException, DataAccessException {
        return get(fund.getSymbol(), date);
    }

    @Override
    public List<Quote> get(Fund fund, Collection<LocalDate> dates)
            throws QuoteUnavailableException, DataAccessException {
        List<Quote> quotes = new ArrayList<Quote>();
        for (LocalDate date : dates) {
            quotes.add(get(fund.getSymbol(), date));
        }
        return quotes;
    }

    private final Pattern pattern = Pattern.compile("^\"(\\w+)\",\"(\\d{4}-\\d{2}-\\d{2})\"");

    private HashMap<String, String> readValuesFromFile(AppEngineFile file) throws Exception {
//        log.info(Thread.currentThread().getName() + " enter read");
        HashMap<String, String> dateStrQuoteStrMap = new HashMap<String, String>();
        FileReadChannel readChannel = fileService.openReadChannel(file, false);
        BufferedReader reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty())
                continue;
            Matcher matcher = pattern.matcher(line);
            matcher.find();
            String date = matcher.group(2);

            dateStrQuoteStrMap.put(date, line);
        }
        readChannel.close();
//        log.info(Thread.currentThread().getName() + " exit read");
        return dateStrQuoteStrMap;
    }

}
