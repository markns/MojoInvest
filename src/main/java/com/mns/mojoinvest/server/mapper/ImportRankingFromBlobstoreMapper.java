package com.mns.mojoinvest.server.mapper;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.AppEngineMapper;
import com.google.appengine.tools.mapreduce.BlobstoreRecordKey;
import com.google.appengine.tools.mapreduce.DatastoreMutationPool;
import org.apache.hadoop.io.NullWritable;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.logging.Logger;

public class ImportRankingFromBlobstoreMapper extends
        AppEngineMapper<BlobstoreRecordKey, byte[], NullWritable, NullWritable> {

    private static final Logger log = Logger.getLogger(ImportRankingFromBlobstoreMapper.class.getName());

    public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    //SPAG,iShares S&P Commodity Producers Agribusiness(SPAG),Alternatives,iShares,true,UK,,,2012-01-16

    @Override
    public void map(BlobstoreRecordKey key, byte[] segment, Context context) {

        String line = new String(segment);

        log.info("At offset: " + key.getOffset());
        log.info("Got value: " + line);

        String[] values = line.split(",");

        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            values[i] = value.replaceAll("\"", "");
        }

        String id = values[0];
        String symbols = values[1];

        if (!id.isEmpty()) {
            Entity quote = new Entity("Ranking", id);
            quote.setProperty("symbols", symbols);

            DatastoreMutationPool mutationPool = this.getAppEngineContext(context)
                    .getMutationPool();
            mutationPool.put(quote);
        }

    }
}