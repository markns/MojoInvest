package com.mns.alphaposition.server.engine.strategy;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.mns.alphaposition.server.engine.model.QuoteDao;
import com.mns.alphaposition.server.engine.model.Fund;
import com.mns.alphaposition.server.engine.model.Quote;
import com.mns.alphaposition.server.engine.params.SimpleRankingStrategyParams;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class SimpleRankingStrategy implements RankingStrategy<SimpleRankingStrategyParams> {

    private QuoteDao quoteDao;

    @Inject
    public SimpleRankingStrategy(QuoteDao quoteDao) {
        this.quoteDao = quoteDao;
    }

    @Override
    public List<Fund> rank(LocalDate rebalanceDate, List<Fund> funds,
                           SimpleRankingStrategyParams params) {

        Collection<Quote> fromQuotes = quoteDao.getAverage(funds, rebalanceDate.minusMonths(params.getPerformanceRange()),
                params.getAveragingRange());
        Collection<Quote> toQuotes = quoteDao.getAverage(funds, rebalanceDate, params.getAveragingRange());

        //TODO: This method should be optimised
        Map<Fund, PerformanceStat> ranker = new HashMap<Fund, PerformanceStat>(funds.size());
        Map<String, Fund> lookup = new HashMap<String, Fund>(funds.size());
        for (Fund fund : funds) {
            ranker.put(fund, new PerformanceStat());
            lookup.put(fund.getSymbol(), fund);
        }
        for (Quote fromQuote : fromQuotes) {
            ranker.get(lookup.get(fromQuote.getSymbol())).fromQuote = fromQuote;
        }
        for (Quote toQuote : toQuotes) {
            ranker.get(lookup.get(toQuote.getSymbol())).toQuote = toQuote;
        }
        Map<Fund, PerformanceStat> rankerNotNull = new HashMap<Fund, PerformanceStat>(funds.size());
        for (Map.Entry<Fund, PerformanceStat> entry : ranker.entrySet()) {
            if (entry.getValue().fromQuote != null && entry.getValue().toQuote != null) {
                rankerNotNull.put(entry.getKey(), entry.getValue());
            }
        }
        for (PerformanceStat stat : rankerNotNull.values()) {
            stat.percentChange = percentageChange(stat.fromQuote, stat.toQuote);
        }

        //Begin guava voodoo
        Ordering<Fund> valueComparator = Ordering.natural().onResultOf(Functions.forMap(rankerNotNull)).reverse()
                .compound(Ordering.natural());
        SortedMap<Fund, PerformanceStat> map = ImmutableSortedMap.copyOf(rankerNotNull, valueComparator);

//        for (Map.Entry<Fund, PerformanceStat> entry : map.entrySet()) {
//            System.out.println(entry.getKey().getSymbol() + " "
//                    + entry.getValue().fromQuote.getClose() + " "
//                    + entry.getValue().toQuote.getClose() + " "
//                    + entry.getValue().percentChange);
//        }

        return new ArrayList<Fund>(map.keySet());
    }

    private static BigDecimal percentageChange(Quote fromQuote, Quote toQuote) {
        return percentageChange(fromQuote.getClose(), toQuote.getClose());
    }

    private static BigDecimal percentageChange(BigDecimal from, BigDecimal to) {
        BigDecimal change = to.subtract(from);
        return change.divide(from, 5, RoundingMode.HALF_EVEN);
    }

    private static class PerformanceStat implements Comparable<PerformanceStat> {
        public Quote fromQuote;
        public Quote toQuote;
        public BigDecimal percentChange;

        @Override
        public int compareTo(PerformanceStat o) {
            return percentChange.compareTo(o.percentChange);
        }
    }

}
