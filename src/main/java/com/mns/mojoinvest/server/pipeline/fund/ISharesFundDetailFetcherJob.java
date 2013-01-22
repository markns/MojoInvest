package com.mns.mojoinvest.server.pipeline.fund;

import com.google.appengine.tools.pipeline.Job2;
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
import java.util.Map;
import java.util.logging.Logger;

public class ISharesFundDetailFetcherJob extends Job2<String, String, String> {

    private static final Logger log = Logger.getLogger(ISharesFundDetailFetcherBatchJob.class.getName());

    @Override
    public Value<String> run(String fundId, String absoluteTicker) {
        log.info("Attempting to retrieve details for funds: " + absoluteTicker);
        FundDao dao = PipelineHelper.getFundDao();

        String html = fetchFundDetailHtml(absoluteTicker);
        Fund fund = buildFund(fundId, html);
        if (fund != null) {
            dao.put(fund);
        }

        String msg = "Finished updating fund: " + fund;
        log.info(msg);
        return immediate(msg);

    }

    private String fetchFundDetailHtml(String absoluteTicker) {
        Client client = PipelineHelper.getClient();
        WebResource r = client.resource("http://uk.ishares.com/en/rc/products/" + absoluteTicker);
        return r.get(String.class);
    }

    @VisibleForTesting
    protected Fund buildFund(String fundId, String html) {
        return buildFund(fundId, scrapeDetails(html));
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

    private Fund buildFund(String fundId, Map<String, String> details) {
        return new Fund(details.get("Ticker"),
                fundId,
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
