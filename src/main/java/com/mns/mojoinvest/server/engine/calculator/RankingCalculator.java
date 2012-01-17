package com.mns.mojoinvest.server.engine.calculator;

import au.com.bytecode.opencsv.CSVReader;
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
import com.mns.mojoinvest.server.util.QuoteUtils;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    @VisibleForTesting
    protected Ranking buildRanking(LocalDate date, RankingParams params, Map<String, BigDecimal> performances) {
        Ordering<String> valueComparator = Ordering.natural()
                .reverse()
                .onResultOf(Functions.forMap(performances))
                .compound(Ordering.natural());
        SortedSet<String> rank = ImmutableSortedMap.copyOf(performances, valueComparator).keySet();
        return new Ranking(date, params, createJoinString(rank));
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


    //--------------------------------


    public static void main(String[] args) throws IOException {

        RankingParams params = new RankingParams(9);
        RankingCalculator calculator = new RankingCalculator();

        Map<LocalDate, List<Quote>> quotes = calculator.readQuoteFiles();

        List<LocalDate> dates = TradingDayUtils
                .getDailySeries(new LocalDate("2000-04-03"), new LocalDate("2012-01-13"), true);

        for (LocalDate date : dates) {
            LocalDate fromDate = TradingDayUtils.rollIfRequired(date.minusMonths(params.getFormationPeriod()));

            List<Quote> fromQuotes = quotes.get(fromDate);
            List<Quote> toQuotes = quotes.get(date);
            if (fromQuotes != null && toQuotes != null) {
                Map<String, BigDecimal> performances = calculator.calculatePerformances(fromQuotes, toQuotes);
                Ranking ranking = calculator.buildRanking(date, params, performances);
                System.out.println(ranking.toCsv());
            }
        }


    }

    private Map<LocalDate, List<Quote>> readQuoteFiles() throws IOException {
        Map<LocalDate, List<Quote>> quoteMap = new HashMap<LocalDate, List<Quote>>();

        String[] files = new String[]{"data/ishares_quotes_tr.csv", "data/ishares_missingquotes_tr.csv"};
        for (String file : files) {
            readQuotesFromFile(quoteMap, file);
        }
        return quoteMap;

    }

    private void readQuotesFromFile(Map<LocalDate, List<Quote>> quoteMap, String file) throws IOException {
        CSVReader reader = new CSVReader(new BufferedReader(new FileReader(file)));
        for (String[] row : reader.readAll()) {
            if ("symbol".equals(row[0]))
                continue;
            LocalDate date = new LocalDate(row[1]);
            if (!quoteMap.containsKey(date)) {
                quoteMap.put(date, new ArrayList<Quote>());
            }
            quoteMap.get(date).add(QuoteUtils.fromStringArray(row));
        }
        reader.close();
    }

    private RankingCalculator() {
    }


}
