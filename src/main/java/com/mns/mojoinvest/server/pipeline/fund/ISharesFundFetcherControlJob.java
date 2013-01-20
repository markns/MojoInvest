package com.mns.mojoinvest.server.pipeline.fund;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job0;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.mns.mojoinvest.server.pipeline.PipelineHelper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
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

public class ISharesFundFetcherControlJob extends Job0<String> {

    private static final Logger log = Logger.getLogger(ISharesFundFetcherControlJob.class.getName());

    private static final int BATCH_SIZE = 30;

    @Override
    public Value<String> run() {

        String html = fetchAllFundsHtml();
        List<String> links = scrapeLinks(html);

        //TODO: Scrape and persist categories
//        scrapeCategories(html);
        log.info("Attempting to retrieve details for " + links.size() + " funds");
        List<String> batch = new ArrayList<String>(BATCH_SIZE);

        List<FutureValue<String>> fundsUpdated = new ArrayList<FutureValue<String>>();

        for (String link : links) {
            batch.add(link);
            if (batch.size() == BATCH_SIZE) {
                List<String> clone = new ArrayList<String>(batch);
                fundsUpdated.add(futureCall(new ISharesFundDetailFetcherBatchJob(), immediate(clone)));
                batch.clear();
            }
        }
        if (batch.size() > 0) {
            fundsUpdated.add(futureCall(new ISharesFundDetailFetcherBatchJob(), immediate(batch)));
        }
        log.info("Fund fetcher control job is complete");
        return futureCall(new MergeListJob(), futureList(fundsUpdated));
    }


    private String fetchAllFundsHtml() {
        Client client = PipelineHelper.getClient();

        client.addFilter(new LoggingFilter());
        WebResource r = client.resource("http://uk.ishares.com/en/rc/products/overview");
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        log.info("Attempting to fetch all funds html");
        String html = r.queryParams(params).get(String.class);
        log.info("Received all funds html");
        return html;
    }

    //    <td class="grid_data fund fund_en string"> <span><a href="/en/rc/products;jsessionid=CF7CC7F2BCAE95A00F65F140A1C55EF7.isharesnet-pea02/ITPS">iShares Barclays $ TIPS</a></span></td>
    @VisibleForTesting
    protected List<String> scrapeLinks(String html) {
        Document doc = Jsoup.parse(html);
        List<String> links = new ArrayList<String>();
        for (Element e : doc.select("table#fund_overview").select("tbody").select("tr")) {
            String link = e.getElementsByTag("a").get(0).attr("href");
            if (link.contains(";jsessionid=")) {
                link = link.replaceFirst(";jsessionid=\\w+\\.\\w+-\\w+\\d+", "");
            }
            links.add(link);
        }
        return links;
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


    private static class MergeListJob extends Job1<String, List<String>> {

        @Override
        public Value<String> run(List<String> strings) {
            return immediate(Joiner.on("\n").join(strings));
        }

    }

}


