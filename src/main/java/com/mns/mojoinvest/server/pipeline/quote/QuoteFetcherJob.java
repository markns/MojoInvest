package com.mns.mojoinvest.server.pipeline.quote;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.joda.time.LocalDate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.ws.rs.core.MultivaluedMap;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class QuoteFetcherJob extends Job2<Quote, Fund, LocalDate> {

    @Override
    public Value<Quote> run(Fund fund, LocalDate date) {
        String html = fetchQuoteHtml(fund.getSymbol());
        Map<String, String> details = scrapeQuoteDetails(html);
        return immediate(buildQuote(fund.getSymbol(), date, details));
    }

    private String fetchQuoteHtml(String symbol) {
        Client c = Client.create();
        c.setReadTimeout(10000);
        c.setConnectTimeout(10000);
        WebResource r = c.resource("http://investing.money.msn.com/investments/etf-list");
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("symbol", symbol);
        return r.queryParams(params).get(String.class);
    }

    private Map<String, String> scrapeQuoteDetails(String html) {
        Document doc = Jsoup.parse(html);
        Map<String, String> details = new HashMap<String, String>();
        details.put("Close", doc.getElementById("quickquoteb").getElementsByClass("lp").text());
        for (Element span : doc.getElementById("area1").select("span")) {
            if ("DETAILS".equals(span.text())) {
                Element tbody = span.parent().parent().select("tbody").get(0);
                for (Element tr : tbody.select("tr")) {
                    details.put(tr.child(0).text(), tr.child(1).text());
                }
            }
        }
        return details;
    }

    private Quote buildQuote(String symbol, LocalDate date, Map<String, String> details) {
        try {
            return new Quote(symbol, date,
                    newBigDecimal(details.get("Open")),
                    newBigDecimal(details.get("Day's High")),
                    newBigDecimal(details.get("Day's Low")),
                    newBigDecimal(details.get("Close")),
                    newBigDecimal(details.get("Bid")),
                    newBigDecimal(details.get("Ask")),
                    newBigDecimal(details.get("Volume")),
                    null, false);
        } catch (NumberFormatException nfe) {
            throw new IllegalStateException("Unable to build quote for " + symbol + " on " + date +
                    " with data " + details);
        }
    }

    private BigDecimal newBigDecimal(String s) {
        if ("NA".equals(s)) {
            return null;
        }
        s = s.replaceAll(",", "");
        if (s.endsWith(" Mil")) {
            s = s.replace(" Mil", "");
            return new BigDecimal(s).multiply(new BigDecimal("1000000"));
        }
        return new BigDecimal(s);
    }

}
