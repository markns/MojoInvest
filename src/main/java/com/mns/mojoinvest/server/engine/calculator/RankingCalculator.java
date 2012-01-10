package com.mns.mojoinvest.server.engine.calculator;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.Ranking;
import com.mns.mojoinvest.server.engine.model.RankingParams;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

public class RankingCalculator {

    private QuoteDao dao;

    public RankingCalculator(QuoteDao dao) {
        this.dao = dao;
    }

    private QuoteDao getQuoteDao() {
        //TODO: Figure out how to inject and serialize DAOs
        ObjectifyFactory factory = ObjectifyService.factory();
        QuoteDao dao = new QuoteDao(factory);
        dao.registerObjects(factory);
        return dao;
    }

    public Ranking rank(LocalDate date, RankingParams params) {
        if (dao == null) {
            //TODO: Figure out how to inject and serialize DAOs
            dao = getQuoteDao();
        }
        List<Quote> toQuotes = dao.query(date);
        LocalDate fromDate = TradingDayUtils.rollIfRequired(date.minusMonths(params.getFormationPeriod()));
        List<Quote> fromQuotes = dao.query(fromDate);
        Map<String, BigDecimal> performances = calculatePerformances(fromQuotes, toQuotes);
        return buildRanking(date, params, performances);
    }

    @VisibleForTesting
    protected Ranking buildRanking(LocalDate date, RankingParams params, Map<String, BigDecimal> performances) {
        Ordering<String> valueComparator = Ordering.natural()
                .reverse()
                .onResultOf(Functions.forMap(performances))
                .compound(Ordering.natural());
        SortedSet<String> rank = ImmutableSortedMap.copyOf(performances, valueComparator).keySet();
        return new Ranking(date, params, createJoinString(rank));
    }

    private Map<String, BigDecimal> calculatePerformances(List<Quote> fromQuotes, List<Quote> toQuotes) {
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

    private static BigDecimal percentageChange(Quote fromQuote, Quote toQuote) {
        return percentageChange(fromQuote.getClose(), toQuote.getClose());
    }

    private static BigDecimal percentageChange(BigDecimal from, BigDecimal to) {
        BigDecimal change = to.subtract(from);
        return change.divide(from, MathContext.DECIMAL32);
    }

    private String createJoinString(Collection coll) {
        Joiner joiner = Joiner.on("|");
        return joiner.join(coll);
    }
}
