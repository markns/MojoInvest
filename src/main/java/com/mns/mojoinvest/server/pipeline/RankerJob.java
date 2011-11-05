package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.model.Ranking;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class RankerJob extends Job1<Ranking, LocalDate> {

    public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Override
    public Value<Ranking> run(LocalDate date) {

        //TODO: Figure out how to inject and serialize DAOs
        ObjectifyFactory factory = ObjectifyService.factory();
        QuoteDao dao = new QuoteDao(factory);
        dao.registerObjects(factory);
        //

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


        }


        return null;
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
