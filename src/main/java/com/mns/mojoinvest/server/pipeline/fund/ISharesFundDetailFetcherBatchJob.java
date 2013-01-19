package com.mns.mojoinvest.server.pipeline.fund;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.common.annotations.VisibleForTesting;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ISharesFundDetailFetcherBatchJob extends Job1<List<Fund>, List<String>> {

    private static final Logger log = Logger.getLogger(ISharesFundDetailFetcherBatchJob.class.getName());

    @Override
    public Value<List<Fund>> run(List<String> symbols) {
        List<Fund> funds = new ArrayList<Fund>();
        log.info("Attempting to retrieve details for batch: " + symbols);
        for (String symbol : symbols) {
            //TODO: Figure out why this fund is screwing up
//            if ("IDVY".equals(symbol))
//                continue;
            Fund fund = runOne(symbol);
            if (fund != null) {
                funds.add(fund);
            }
        }
        return immediate(funds);
    }

    public Fund runOne(String symbol) {
        log.info("Attempting to fetch html for " + symbol);
        String html = fetchFundDetailHtml(symbol);
        Fund fund = buildFund(html);
        log.info("Constructed fund " + fund);
        return fund;
    }

    private String fetchFundDetailHtml(String symbol) {
        Client c = Client.create();
//        c.addFilter(new LoggingFilter(System.out));

        c.setReadTimeout(10000);
        c.setConnectTimeout(10000);
        WebResource r = c.resource("http://uk.ishares.com/en/rc/products/" + symbol);
        return r.get(String.class);
    }

    @VisibleForTesting
    protected Fund buildFund(String html) {
        return buildFund(scrapeDetails(html));
    }

    private Map<String, String> scrapeDetails(String html) {

        Map<String, String> details = new HashMap<String, String>();
        Document doc = Jsoup.parse(html);

        details.put("Name", doc.select("h1#FUND_OVERVIEWTitle").text().split("\\s+(?=\\S*+$)")[0]);
        details.put("Description", doc.select("div#fundData p").text());

        for (Element e : doc.select("#fund_facts tbody tr")) {
            details.put(e.select(".c1").text(), e.select(".c2").text());
        }

        for (Element e : doc.select("#fund_product tbody tr")) {
            details.put(e.select(".c1").text(), e.select(".c2").text());
        }

        for (Element e : doc.select("#fund_benchmark tbody tr")) {
            details.put(e.select(".c1").text(), e.select(".c2").text());
        }


        return details;
    }

    private static DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yy");

    private Fund buildFund(Map<String, String> details) {
        return new Fund(details.get("Ticker"),
                details.get("Name"),
                details.get("category"),
                "iShares",
                true,
                details.get("Domicile"),
                details.get("Benchmark Name"),
                details.get("Description"),
                fmt.parseLocalDate(details.get("Inception Date")));
    }


}
