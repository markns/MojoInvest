package com.mns.mojoinvest.server.mapper;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.mapreduce.AppEngineMapper;
import com.google.appengine.tools.mapreduce.BlobstoreRecordKey;
import com.google.appengine.tools.mapreduce.DatastoreMutationPool;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.QuoteDao;
import org.apache.hadoop.io.NullWritable;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.logging.Logger;

public class PerformanceRankingMapper extends
        AppEngineMapper<BlobstoreRecordKey, byte[], NullWritable, NullWritable> {

    public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    private static final Logger log = Logger.getLogger(PerformanceRankingMapper.class.getName());

    @Inject
    private QuoteDao dao;

    @Override
    public void map(BlobstoreRecordKey key, byte[] segment, Context context) {

        String line = new String(segment);

        log.info("At offset: " + key.getOffset());
        log.info("Got value: " + line);

        LocalDate date = fmt.parseDateTime(line.trim()).toLocalDate();

        List<Quote> toQuotes = dao.query(date);
        List<Quote> fromQuotes = dao.query(date.minusMonths(9));

        Map<String, BigDecimal> ranker = buildRanker(toQuotes, fromQuotes);

        if (ranker.size() > 0) {

            Ordering<String> valueComparator = Ordering.natural()
                    .reverse()
                    .onResultOf(Functions.forMap(ranker))
                    .compound(Ordering.natural());

            SortedSet<String> rank = ImmutableSortedMap.copyOf(ranker, valueComparator).keySet();

            String m9 = createRankString(rank);

            Entity ranking = new Entity("Ranking", fmt.print(date));
            ranking.setUnindexedProperty("m9", new Text(m9));

            DatastoreMutationPool mutationPool = this.getAppEngineContext(context)
                    .getMutationPool();
            mutationPool.put(ranking);
        }
    }

    private Map<String, BigDecimal> buildRanker(List<Quote> toQuotes, List<Quote> fromQuotes) {
        Map<String, Quote> fromQuoteMap = new HashMap<String, Quote>(fromQuotes.size());
        for (Quote quote : fromQuotes) {
            fromQuoteMap.put(quote.getSymbol(), quote);
        }

        Map<String, BigDecimal> ranker = new HashMap<String, BigDecimal>();
        for (Quote toQuote : toQuotes) {
            if (fromQuoteMap.containsKey(toQuote.getSymbol())) {
                ranker.put(toQuote.getSymbol(), percentageChange(fromQuoteMap.get(toQuote.getSymbol()), toQuote));
            }
        }
        return ranker;
    }

    private String createRankString(SortedSet<String> rank) {
        List<String> rankList = new ArrayList<String>(rank);
        Joiner joiner = Joiner.on("|");
        if (rankList.size() > 50) {
            return joiner.join(rankList.subList(0, 50));
        }
        return joiner.join(rankList);
    }

    private static BigDecimal percentageChange(Quote fromQuote, Quote toQuote) {
        return percentageChange(fromQuote.getClose(), toQuote.getClose());
    }

    private static BigDecimal percentageChange(BigDecimal from, BigDecimal to) {
        BigDecimal change = to.subtract(from);
        return change.divide(from, 5, RoundingMode.HALF_EVEN);
    }

}