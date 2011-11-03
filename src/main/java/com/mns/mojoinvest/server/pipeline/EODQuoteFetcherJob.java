package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;

import java.util.ArrayList;
import java.util.List;

public class EODQuoteFetcherJob extends Job1<List<Quote>, List<Fund>> {

    @Override
    public Value<List<Quote>> run(List<Fund> funds) {

        List<FutureValue<Quote>> quotes = new ArrayList<FutureValue<Quote>>();

        for (Fund fund : funds) {
            quotes.add(futureCall(new EODQuoteUrl(), immediate(fund.getSymbol())));
        }
        return futureList(quotes);
    }

    private static class EODQuoteUrl extends Job1<Quote, String> {

        @Override
        public Value<Quote> run(String symbol) {
            return null;
        }
    }
}
