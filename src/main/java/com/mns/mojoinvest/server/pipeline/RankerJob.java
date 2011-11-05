package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.common.base.Joiner;
import com.mns.mojoinvest.server.engine.model.Quote;
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
