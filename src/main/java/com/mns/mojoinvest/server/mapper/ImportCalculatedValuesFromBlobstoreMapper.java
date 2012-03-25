package com.mns.mojoinvest.server.mapper;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.AppEngineMapper;
import com.google.appengine.tools.mapreduce.BlobstoreRecordKey;
import com.google.appengine.tools.mapreduce.DatastoreMutationPool;
import org.apache.hadoop.io.NullWritable;

import java.util.logging.Logger;

public class ImportCalculatedValuesFromBlobstoreMapper extends
        AppEngineMapper<BlobstoreRecordKey, byte[], NullWritable, NullWritable> {

    private static final Logger log = Logger.getLogger(ImportCalculatedValuesFromBlobstoreMapper.class.getName());

    //"1996-06-27|EWM|SMA|4","8.425000"

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
        String value = values[1];

        if (!id.isEmpty()) {
            Entity calculatedValue = new Entity("CalculatedValue", id);
            calculatedValue.setUnindexedProperty("value", value);

            DatastoreMutationPool mutationPool = this.getAppEngineContext(context)
                    .getMutationPool();
            mutationPool.put(calculatedValue);
        }

    }
}