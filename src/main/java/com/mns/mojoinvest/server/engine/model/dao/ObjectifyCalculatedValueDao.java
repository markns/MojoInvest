package com.mns.mojoinvest.server.engine.model.dao;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.mns.mojoinvest.server.engine.model.BlobstoreKeyRecord;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.Channels;
import java.util.*;
import java.util.logging.Logger;

public class ObjectifyCalculatedValueDao implements CalculatedValueDao {


    private static final Logger log = Logger.getLogger(ObjectifyCalculatedValueDao.class.getName());

    private static boolean objectsRegistered;
    private final BlobstoreKeyRecordDao recordDao;

    private final FileService fileService = FileServiceFactory.getFileService();

    @Inject
    public ObjectifyCalculatedValueDao(BlobstoreKeyRecordDao recordDao) {
        this.recordDao = recordDao;
    }


    @Override
    public Key<CalculatedValue> put(CalculatedValue cv) {
//        return ofy().put(cv);
        return null;
    }

    @Override
    public Map<Key<CalculatedValue>, CalculatedValue> put(List<CalculatedValue> cvs) {
//        return ofy().put(cvs);
        return null;
    }

    @Override
    public Map<String, Map<LocalDate, BigDecimal>> get(Collection<Fund> funds, String type, int period) {
        Map<String, Map<LocalDate, BigDecimal>> cvs = new HashMap<String, Map<LocalDate, BigDecimal>>(funds.size());

        for (Fund fund : funds) {
            BlobstoreKeyRecord record = recordDao.get(fund.getSymbol() + "|" + type + "|" + period);
            AppEngineFile file = fileService.getBlobFile(record.getBlobKey());
            try {
                cvs.put(fund.getSymbol(), readValuesFromFile(file));
            } catch (IOException e) {
                //TODO:throw new DataAccessException(e);
                e.printStackTrace();
            }
        }
        return cvs;
    }

    private static final Splitter SPLITTER = Splitter.on('|')
            .trimResults()
            .omitEmptyStrings();

    private Map<LocalDate, BigDecimal> readValuesFromFile(AppEngineFile file) throws IOException {
        // Later, read from the file using the file API
        boolean lock = false; // Let other people read at the same time
        FileReadChannel readChannel = fileService.openReadChannel(file, false);

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
