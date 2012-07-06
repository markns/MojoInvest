package com.mns.mojoinvest.server.engine.model.dao;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.BlobstoreKeyRecord;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import org.apache.commons.lang.NotImplementedException;
import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlobstoreQuoteDao implements QuoteDao {

    private final BlobstoreKeyRecordDao recordDao;

    private final FileService fileService = FileServiceFactory.getFileService();

    @Inject
    public BlobstoreQuoteDao(BlobstoreKeyRecordDao recordDao) {
        this.recordDao = recordDao;
    }

    @Override
    public void registerObjects(ObjectifyFactory ofyFactory) {
        throw new NotImplementedException();
    }

    @Override
    public Key<Quote> put(Quote quote) {
        throw new NotImplementedException();
    }

    @Override
    public Map<Key<Quote>, Quote> put(Iterable<Quote> quotes) {
        throw new NotImplementedException();
    }

    //TODO: replace with memCache
    Map<String, Map<String, String>> quoteCache = new HashMap<String, Map<String, String>>();

    @Override
    public Quote get(String symbol, LocalDate date) {

        if (!quoteCache.containsKey(symbol) || !quoteCache.get(symbol).containsKey(date.toString())) {
            BlobstoreKeyRecord record = recordDao.get(symbol + "|" + date.getYear());
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

        return Quote.fromStr(quoteStr);
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
    public Quote get(Fund fund, LocalDate date) {
        return get(fund.getSymbol(), date);
    }

    @Override
    public Collection<Quote> get(Fund fund, Collection<LocalDate> dates) {
        Collection<Quote> quotes = new HashSet<Quote>();
        for (LocalDate date : dates) {
            quotes.add(get(fund.getSymbol(), date));
        }
        return quotes;
    }

    @Override
    public Collection<Quote> get(Collection<String> symbols, Collection<LocalDate> dates) {
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
