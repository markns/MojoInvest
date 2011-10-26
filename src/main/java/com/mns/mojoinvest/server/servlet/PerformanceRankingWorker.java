/* Copyright (c) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mns.mojoinvest.server.servlet;

import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.*;
import com.mns.mojoinvest.server.servlet.util.ParameterNotFoundException;
import com.mns.mojoinvest.server.servlet.util.ParameterParser;
import org.joda.time.LocalDate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Task Queue worker servlet that offsets a counter by a delta.
 */
@Singleton
public class PerformanceRankingWorker extends HttpServlet {

    @Inject
    private QuoteDao dao;

    @Inject
    private RankingDao rankingDao;

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        ParameterParser parser = new ParameterParser(req);

        try {
            LocalDate date = parser.getLocalDateParameter("date");
            int range = parser.getIntParameter("range");


            List<Quote> toQuotes = dao.query(date);
            List<Quote> fromQuotes = dao.query(date.minusMonths(range));

            Map<String, BigDecimal> ranker = buildRanker(toQuotes, fromQuotes);

            if (ranker.size() > 0) {

                Ordering<String> valueComparator = Ordering.natural()
                        .reverse()
                        .onResultOf(Functions.forMap(ranker))
                        .compound(Ordering.natural());

                SortedSet<String> rank = ImmutableSortedMap.copyOf(ranker, valueComparator).keySet();

                List<String> rankList = new ArrayList<String>(rank);
                Joiner joiner = Joiner.on("|");
                String symbols = joiner.join(rankList);

                RankingText rankingText = new RankingText(date + "|" + range, symbols, "");

                rankingDao.put(rankingText);
            }


        } catch (ParameterNotFoundException e) {
            throw new IOException(e);
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
