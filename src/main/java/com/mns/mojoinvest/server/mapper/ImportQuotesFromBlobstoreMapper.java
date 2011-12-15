package com.mns.mojoinvest.server.mapper;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.AppEngineMapper;
import com.google.appengine.tools.mapreduce.BlobstoreRecordKey;
import com.google.appengine.tools.mapreduce.DatastoreMutationPool;
import com.mns.mojoinvest.server.util.QuoteUtils;
import org.apache.hadoop.io.NullWritable;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.logging.Logger;

public class ImportQuotesFromBlobstoreMapper extends
        AppEngineMapper<BlobstoreRecordKey, byte[], NullWritable, NullWritable> {

    private static final Logger log = Logger.getLogger(ImportQuotesFromBlobstoreMapper.class.getName());

    public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

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
        LocalDate date  = fmt.parseDateTime(values[1]).toLocalDate();
        String open = values[2];
        String high = values[3];
        String low = values[4];
        String close = values[5];
        String volume = values[6];
        String adjClose = values[7];
        boolean rolled = Boolean.parseBoolean(values[8]);

        if (!symbol.isEmpty()) {
            Entity quote = new Entity("Quote", QuoteUtils.quoteId(symbol, date));
            quote.setProperty("symbol", symbol);
            quote.setProperty("date", date.toDateMidnight().toDate());
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