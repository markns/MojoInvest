package com.mns.mojoinvest.server.engine.calculator;

import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.Ranking;
import com.mns.mojoinvest.server.engine.model.RankingParams;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.assertEquals;

public class RankingProcessorTests {

    private final QuoteDao quoteDao = new QuoteDao(ObjectifyService.factory());


    @Test
    public void testRanking() {
        List<Quote> fromQuotes = new ArrayList<Quote>();
        fromQuotes.add(new Quote("A", new LocalDate(2011, 1, 7), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        fromQuotes.add(new Quote("B", new LocalDate(2011, 1, 7), new BigDecimal("2"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        fromQuotes.add(new Quote("C", new LocalDate(2011, 1, 7), new BigDecimal("3"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        fromQuotes.add(new Quote("D", new LocalDate(2011, 1, 7), new BigDecimal("4"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        fromQuotes.add(new Quote("E", new LocalDate(2011, 1, 7), new BigDecimal("5"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));

        List<Quote> toQuotes = new ArrayList<Quote>();
        toQuotes.add(new Quote("A", new LocalDate(2011, 7, 7), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        toQuotes.add(new Quote("B", new LocalDate(2011, 7, 7), new BigDecimal("2"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        toQuotes.add(new Quote("C", new LocalDate(2011, 7, 7), new BigDecimal("3"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        toQuotes.add(new Quote("D", new LocalDate(2011, 7, 7), new BigDecimal("4"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));
        toQuotes.add(new Quote("E", new LocalDate(2011, 7, 7), new BigDecimal("5"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("1"), null, null, new BigDecimal("1"), new BigDecimal("1"), false));

    }

    @Test
    public void testBuildRanking() {

        Map<String, BigDecimal> performances = new HashMap<String, BigDecimal>();
        performances.put("A", new BigDecimal("43.7"));
        performances.put("B", new BigDecimal("34.5"));
        performances.put("C", new BigDecimal("65.3"));
        performances.put("D", new BigDecimal("21.8"));
        performances.put("E", new BigDecimal("88.6"));

        RankingCalculator rankingCalculator = new RankingCalculator(quoteDao);
        Ranking ranking = rankingCalculator.buildRanking(new LocalDate(2011, 7, 7), new RankingParams(6),
                performances);

        assertEquals(Arrays.asList("E", "C", "A", "B", "D"), ranking.getSymbols());

    }

}
