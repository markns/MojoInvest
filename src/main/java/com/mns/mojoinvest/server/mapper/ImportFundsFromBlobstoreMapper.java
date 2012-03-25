package com.mns.mojoinvest.server.mapper;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.AppEngineMapper;
import com.google.appengine.tools.mapreduce.BlobstoreRecordKey;
import com.google.appengine.tools.mapreduce.DatastoreMutationPool;
import org.apache.hadoop.io.NullWritable;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.logging.Logger;

public class ImportFundsFromBlobstoreMapper extends
        AppEngineMapper<BlobstoreRecordKey, byte[], NullWritable, NullWritable> {

    private static final Logger log = Logger.getLogger(ImportFundsFromBlobstoreMapper.class.getName());

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

        String symbol = values[0];
        String name = values[1];
        String category = values[2];
        String provider = values[3];
        boolean active = Boolean.parseBoolean(values[4]);
        String country = values[5];
        String index = values[6];
        String overview = values[7];
        LocalDate inceptionDate = fmt.parseDateTime(values[8]).toLocalDate();

        if (!symbol.isEmpty()) {
            Entity quote = new Entity("Fund", symbol);
            quote.setProperty("name", name);
            quote.setProperty("category", category);
            quote.setProperty("provider", provider);
            quote.setProperty("active", active);
            quote.setProperty("country", country);
            quote.setProperty("index", index);
            quote.setProperty("overview", overview);
            quote.setProperty("inceptionDate", inceptionDate.toDateMidnight().toDate());

            DatastoreMutationPool mutationPool = this.getAppEngineContext(context)
                    .getMutationPool();
            mutationPool.put(quote);
        }

    }
}