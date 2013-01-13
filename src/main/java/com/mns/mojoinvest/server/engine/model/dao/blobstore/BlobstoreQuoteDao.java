package com.mns.mojoinvest.server.engine.model.dao.blobstore;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.*;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.BlobstoreEntryRecord;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.DataAccessException;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.objectify.ObjectifyEntryRecordDao;
import com.mns.mojoinvest.server.util.QuoteUtils;
import org.apache.commons.lang.NotImplementedException;
import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlobstoreQuoteDao implements QuoteDao {

    private final ObjectifyEntryRecordDao recordDao;

    private final FileService fileService = FileServiceFactory.getFileService();

    @Inject
    public BlobstoreQuoteDao(ObjectifyEntryRecordDao recordDao) {
        this.recordDao = recordDao;
    }

    @Override
    public void registerObjects(ObjectifyFactory ofyFactory) {
        recordDao.registerObjects(ofyFactory);
    }

    @Override
    public Key<Quote> put(Quote quote) {
        throw new NotImplementedException();
    }

    @Override
    public Map<Key<Quote>, Quote> put(Iterable<Quote> quotes) {
        //Check if quote exists already
        //Check if record (symbol|year) exists
        Map<String, List<Quote>> map = new HashMap<String, List<Quote>>();
        for (Quote quote : quotes) {
            String key = quote.getSymbol() + "|" + quote.getDate().getYear();
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<Quote>());
            }
            map.get(key).add(quote);
        }

        for (Map.Entry<String, List<Quote>> e : map.entrySet()) {

            Map<String, String> s = new HashMap<String, String>();
            BlobstoreEntryRecord record = recordDao.get(e.getKey());
            if (record != null) {
                AppEngineFile file = fileService.getBlobFile(record.getBlobKey());
                try {
                    s.putAll(readValuesFromFile(file));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            for (Quote quote : e.getValue()) {
                s.put(quote.getDate().toString(), QuoteUtils.toString(quote));
            }

            try {
                //write all values in s to a new AppEngineFile
                AppEngineFile file = writeValuesToBlob(e.getKey(), s.values());
                //Update blobstoreEntryRecordDao - add new key, delete old one
                writeBlobstoreKeyRecord(e.getKey(), file);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }


//        for (AppEngineFile file : files.values()) {
//            try {
//                FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
//                writeChannel.write(ByteBuffer.wrap("And miles to go before I sleep.".getBytes()));
//                writeChannel.closeFinally();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        return null;
    }

    private void writeBlobstoreKeyRecord(String key, AppEngineFile file) {
        // Now read from the file using the Blobstore API
        BlobKey blobKey = fileService.getBlobKey(file);
        BlobstoreEntryRecord record = new BlobstoreEntryRecord(key, blobKey);
        recordDao.put(record);
    }

    private AppEngineFile writeValuesToBlob(String key, Collection<String> values) throws IOException {
        // Create a new Blob file with mime-type "text/plain"
        AppEngineFile file = fileService.createNewBlobFile("text/plain", key);

        // Open a channel to write to it
        boolean lock = true;
        FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

        // Different standard Java ways of writing to the channel
        // are possible. Here we use a PrintWriter:
        PrintWriter writer = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
        for (String value : values) {
            writer.println(value);
        }

        // Close without finalizing and save the file path for writing later
        writer.close();

        // Now finalize
        writeChannel.closeFinally();
        return file;
    }


    //TODO: replace with memCache
    Map<String, Map<String, String>> quoteCache = new HashMap<String, Map<String, String>>();

    @Override
    public Quote get(String symbol, LocalDate date) throws DataAccessException {

        if (!quoteCache.containsKey(symbol) || !quoteCache.get(symbol).containsKey(date.toString())) {
            BlobstoreEntryRecord record = recordDao.get(symbol + "|" + date.getYear());
            AppEngineFile file = fileService.getBlobFile(record.getBlobKey());

            try {
                HashMap<String, String> dateStrQuoteStrMap = readValuesFromFile(file);
                if (!quoteCache.containsKey(symbol)) {
                    quoteCache.put(symbol, new HashMap<String, String>());
                }
                quoteCache.get(symbol).putAll(dateStrQuoteStrMap);

            } catch (IOException e) {
                //TODO: Throw exception
                e.printStackTrace();
            }
        }

        String quoteStr = quoteCache.get(symbol).get(date.toString());

        if (quoteStr == null || quoteStr.isEmpty())
            throw new DataAccessException("Unable to find quote for " + symbol + " on " + date);
        return QuoteUtils.fromString(quoteStr);
    }


    private final Pattern pattern = Pattern.compile("^\"(\\w+)\",\"(\\d{4}-\\d{2}-\\d{2})\"");

    private HashMap<String, String> readValuesFromFile(AppEngineFile file) throws IOException {
        boolean lock = false; // Let other people read at the same time
        FileReadChannel readChannel = fileService.openReadChannel(file, lock);
        BufferedReader reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));

        HashMap<String, String> dateStrQuoteStrMap = new HashMap<String, String>();
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
        return dateStrQuoteStrMap;
    }

    @Override
    public Quote get(Fund fund, LocalDate date) throws DataAccessException {
        return get(fund.getSymbol(), date);
    }

    @Override
    public Collection<Quote> get(Fund fund, Collection<LocalDate> dates) throws DataAccessException {
        Collection<Quote> quotes = new HashSet<Quote>();
        for (LocalDate date : dates) {
            quotes.add(get(fund.getSymbol(), date));
        }
        return quotes;
    }

    @Override
    public Collection<Quote> get(Collection<String> symbols, Collection<LocalDate> dates) throws DataAccessException {
        Collection<Quote> quotes = new HashSet<Quote>();
        for (String symbol : symbols) {
            for (LocalDate date : dates) {
                quotes.add(get(symbol, date));
            }
        }
        return quotes;
    }

    @Override
    public List<Quote> query(String symbol) {
        throw new NotImplementedException();
    }

    @Override
    public List<Quote> query(LocalDate date) {
        throw new NotImplementedException();
    }
}
