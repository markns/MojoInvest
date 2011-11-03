package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.ws.rs.core.MultivaluedMap;
import java.util.logging.Logger;

public class FundDetailFetcherJob extends Job1<Fund, String> {

    private static final Logger log = Logger.getLogger(FundDetailFetcherJob.class.getName());

    public static void main(String[] args) {

        FundDetailFetcherJob job = new FundDetailFetcherJob();
        Value<Fund> fund = job.run("AADR");
        ImmediateValue<Fund> im = (ImmediateValue<Fund>) fund;
        System.out.println(im);
    }

    @Override
    public Value<Fund> run(String symbol) {
        String html = fetchFundDetailHtml(symbol);
        Details details = scrapeDetails(html, symbol);
        return immediate(buildFund(details));
    }

    private String fetchFundDetailHtml(String symbol) {
        Client c = Client.create();
        c.setReadTimeout(10000);
        c.setConnectTimeout(10000);
        WebResource r = c.resource("http://moneycentral.msn.com/investor/partsub/funds/etfsnapshot.asp");
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("symbol", symbol);
        return r.queryParams(params).get(String.class);
    }

    private Details scrapeDetails(String html, String symbol) {
        Details details = new Details(symbol);
        Document doc = Jsoup.parse(html);

        details.name = doc.getElementById("quickquote").getElementsByClass("cn").get(0).child(0).text();

        for (Element element : doc.select("span")) {
            if ("QUICK STATS".equals(element.text())) {
                Element tbody = element.parent().parent().getElementsByTag("tbody").get(0);
                details.category = tbody.child(3).child(1).text();
                details.provider = tbody.child(4).child(1).text();
                details.index = tbody.child(5).child(1).text();
                details.inceptionDate = tbody.child(6).child(1).text();
            }

            if ("OVERVIEW".equals(element.text())) {
                details.overview = element.parent().parent().ownText();
            }
        }
        return details;
    }


    private Fund buildFund(Details details) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
        try {
            return new Fund(details.symbol, details.name, details.category, details.provider, true, "US",
                    details.index, details.overview, fmt.parseDateTime(details.inceptionDate).toLocalDate());
        } catch (IllegalArgumentException e) {
            log.severe("Exception while creating fund with fields: " + details.symbol + " " +
            details.name + " " + details.inceptionDate);
        }
        return null;
    }

    public static class Details {
        String name;
        String symbol;
        String provider;
        String category;
        String index;
        String inceptionDate;
        String overview;

        private Details(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        @Override
        public String toString() {
            return name + " " + symbol + " " + provider + " " + category + " " + index + " " + inceptionDate;
        }
    }


}
