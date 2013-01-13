package com.mns.mojoinvest.server.pipeline.fund;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job0;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.common.annotations.VisibleForTesting;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ISharesFundFetcherJob extends Job0<List<Fund>> {

    private static final Logger log = Logger.getLogger(ISharesFundFetcherJob.class.getName());

    private static final int BATCH_SIZE = 20;

    @Override
    public Value<List<Fund>> run() {
        String html = fetchAllFundsHtml();
        List<String> symbols = scrapeSymbols(html);

        //TODO: Scrape and persist categories
//        scrapeCategories(html);

        log.info("Attempting to retrieve details for " + symbols.size() + " funds");
        List<FutureValue<List<Fund>>> fundLists = new ArrayList<FutureValue<List<Fund>>>();
        List<String> batch = new ArrayList<String>(BATCH_SIZE);

        for (String symbol : symbols) {
            batch.add(symbol);
            if (batch.size() == BATCH_SIZE) {
                List<String> clone = new ArrayList<String>(batch);
                fundLists.add(futureCall(new ISharesFundDetailFetcherBatchJob(), immediate(clone)));
                batch.clear();
            }
        }
        if (batch.size() > 0) {
            fundLists.add(futureCall(new ISharesFundDetailFetcherBatchJob(), immediate(batch)));
        }

        return futureCall(new MergeFundListJob(), futureList(fundLists));
    }


    private String fetchAllFundsHtml() {
        Client c = Client.create();
        c.setReadTimeout(60000);
        c.setConnectTimeout(60000);

        WebResource r = c.resource("http://uk.ishares.com/en/rc/products/overview");
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        log.info("Attempting to fetch all funds html");
        String html = r.queryParams(params).get(String.class);
        log.info("Received all funds html");
        return html;
    }

    @VisibleForTesting
    protected List<String> scrapeSymbols(String html) {
        Document doc = Jsoup.parse(html);

        List<String> symbols = new ArrayList<String>();
        for (Element e : doc.select("table#fund_overview").select("tbody").select("tr")) {
            symbols.add(e.select("td.ticker").text());
        }

        return symbols;
    }

    @VisibleForTesting
    protected void scrapeCategories(String html) {
        Document doc = Jsoup.parse(html);

        String js = doc.select("div#left script").get(2).data();

        Pattern pattern = Pattern.compile("return (\\{.+\\})");

        Matcher matcher = pattern.matcher(js);
        matcher.find();

        String json = matcher.group(1).replaceAll("s0", "\"s0\"");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        JsonFactory factory = mapper.getJsonFactory();
        try {
            JsonParser jp = factory.createJsonParser(json);
            JsonNode root = mapper.readTree(jp);

//            System.out.println(root);
//            for (JsonNode node1 : root.get("children")) {
//                System.out.println(node1);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static class MergeFundListJob extends Job1<List<Fund>, List<List<Fund>>> {

        @Override
        public Value<List<Fund>> run(List<List<Fund>> lists) {
            List<Fund> funds = new ArrayList<Fund>();
            for (List<Fund> list : lists) {
                funds.addAll(list);
            }
            return immediate(funds);
        }
    }

}


