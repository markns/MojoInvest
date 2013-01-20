package com.mns.mojoinvest.server.pipeline.fund;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.common.annotations.VisibleForTesting;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.pipeline.PipelineHelper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ISharesFundDetailFetcherBatchJob extends Job1<String, List<String>> {

    private static final Logger log = Logger.getLogger(ISharesFundDetailFetcherBatchJob.class.getName());

    @Override
    public Value<String> run(List<String> links) {
        log.info("Attempting to retrieve details for funds: " + links);
        FundDao dao = PipelineHelper.getFundDao();
        for (String link : links) {

            Fund fund = runOne(link);
            if (fund != null) {
                dao.put(fund);
            }
        }
        String msg = "Finished updating funds: " + links;
        log.info(msg);
        return immediate(msg);
    }

    public Fund runOne(String link) {
        log.info("Attempting to fetch html at " + link);
        String html = fetchFundDetailHtml(link);
        Fund fund = buildFund(html);
        log.info("Constructed fund " + fund);
        return fund;
    }

    private String fetchFundDetailHtml(String link) {
        Client client = PipelineHelper.getClient();
        WebResource r = client.resource("http://uk.ishares.com" + link);
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
