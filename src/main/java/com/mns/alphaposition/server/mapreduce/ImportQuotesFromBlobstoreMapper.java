package com.mns.alphaposition.server.mapreduce;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.AppEngineMapper;
import com.google.appengine.tools.mapreduce.BlobstoreRecordKey;
import com.google.appengine.tools.mapreduce.DatastoreMutationPool;
import org.apache.hadoop.io.NullWritable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class ImportQuotesFromBlobstoreMapper extends
        AppEngineMapper<BlobstoreRecordKey, byte[], NullWritable, NullWritable> {

    private static final Logger log = Logger.getLogger(ImportQuotesFromBlobstoreMapper.class.getName());

    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

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

        String symbol = values[0];
        Date date;
        try {
            date = fmt.parse(values[1]);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
        String open = values[2];
        String high = values[3];
        String low = values[4];
        String close = values[5];
        String volume = values[6];
        String adjClose = values[7];
        boolean rolled = false;

        if (!symbol.isEmpty()) {
            Entity quote = new Entity("Quote", fmt.format(date) + " " + symbol);
            quote.setProperty("symbol", symbol);
            quote.setProperty("date", date);
            quote.setUnindexedProperty("open", open);
            quote.setUnindexedProperty("high", high);
            quote.setUnindexedProperty("low", low);
            quote.setUnindexedProperty("close", close);
            quote.setUnindexedProperty("volume", volume);
            quote.setUnindexedProperty("adjClose", adjClose);
            quote.setProperty("rolled", rolled);

            DatastoreMutationPool mutationPool = this.getAppEngineContext(context)
                    .getMutationPool();
            mutationPool.put(quote);
        }

    }
}