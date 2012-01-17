package com.mns.mojoinvest.server.mapper;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.mapreduce.AppEngineMapper;
import org.apache.hadoop.io.NullWritable;

public class VolatilityCalculatorMapper extends
        AppEngineMapper<Key, Entity, NullWritable, NullWritable> {


}
