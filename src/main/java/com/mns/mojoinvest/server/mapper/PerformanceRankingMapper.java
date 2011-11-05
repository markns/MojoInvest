package com.mns.mojoinvest.server.mapper;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.mapreduce.AppEngineMapper;
import com.google.appengine.tools.mapreduce.BlobstoreRecordKey;
import com.google.appengine.tools.mapreduce.DatastoreMutationPool;
import com.mns.mojoinvest.server.engine.calculator.RankingCalculator;
import com.mns.mojoinvest.server.engine.model.Ranking;
import com.mns.mojoinvest.server.engine.model.RankingParams;
import org.apache.hadoop.io.NullWritable;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.logging.Logger;

public class PerformanceRankingMapper extends
        AppEngineMapper<BlobstoreRecordKey, byte[], NullWritable, NullWritable> {

    public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    private static final Logger log = Logger.getLogger(PerformanceRankingMapper.class.getName());

    @Override
    public void map(BlobstoreRecordKey key, byte[] segment, Context context) {
        String line = new String(segment);
        log.info("Calculating performance ranks for: " + line);
        LocalDate date = fmt.parseDateTime(line.trim()).toLocalDate();

        int performanceRange = Integer.parseInt(context.getConfiguration()
                .get("mapreduce.mapper.performance.range"));

        RankingCalculator calculator = new RankingCalculator();
        RankingParams params = new RankingParams(performanceRange);
        Ranking ranking = calculator.rank(date, params);

        Entity entity = new Entity("Ranking", Ranking.createId(date, params));
        entity.setUnindexedProperty("symbols", new Text(ranking.getSymbolsStr()));

        DatastoreMutationPool mutationPool = this.getAppEngineContext(context)
                .getMutationPool();
        mutationPool.put(entity);
    }
}