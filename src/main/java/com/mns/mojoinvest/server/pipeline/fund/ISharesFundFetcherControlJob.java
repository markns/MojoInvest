package com.mns.mojoinvest.server.pipeline.fund;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job0;
import com.google.appengine.tools.pipeline.Value;
import com.google.common.annotations.VisibleForTesting;
import com.mns.mojoinvest.server.pipeline.GenericPipelines;
import com.mns.mojoinvest.server.pipeline.PipelineException;
import com.mns.mojoinvest.server.pipeline.PipelineHelper;
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
import org.jsoup.select.Elements;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ISharesFundFetcherControlJob extends Job0<String> {

    private static final Logger log = Logger.getLogger(ISharesFundFetcherControlJob.class.getName());

    @Override
    public Value<String> run() {

        String html = fetchAllFundsHtml();

        Map<String, String> funds = scrapeFunds(html);
//        scrapeCategories(html);

        log.info("Attempting to retrieve details for " + funds.size() + " funds");
        List<FutureValue<String>> fundsUpdated = new ArrayList<FutureValue<String>>();

//        int c = 0;
        for (Map.Entry<String, String> fund : funds.entrySet()) {
            fundsUpdated.add(futureCall(new ISharesFundDetailFetcherJob(), immediate(fund.getKey()), immediate(fund.getValue())));
//            if (c++ == 2)
//                break;
        }
        log.info("Fund fetcher control job is complete");
        return futureCall(new GenericPipelines.MergeListJob(), futureList(fundsUpdated));
    }

    private Map<String, String> scrapeFunds(String html) throws PipelineException {
        Document doc = Jsoup.parse(html);
        Elements scripts = doc.getElementsByTag("script");
        String json = "";
        for (Element script : scripts) {
            if (script.html().contains("var fundMenu")) {
                Pattern pattern = Pattern.compile("return (\\{.*\\})");
                Matcher matcher = pattern.matcher(script.html());
                matcher.find();
                json = matcher.group(1).replaceAll("s0", "\"s0\"");
                break;
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        JsonFactory factory = mapper.getJsonFactory();
        JsonNode root;
        try {
            JsonParser jp = factory.createJsonParser(json);
            root = mapper.readTree(jp);
        } catch (IOException e) {
            throw new PipelineException("Unable to parse json for fund static", e);
        }

        Map<String, String> map = new HashMap<String, String>();
        for (JsonNode level1 : root.get("children")) {

            for (JsonNode level2 : level1.get("children")) {

                for (JsonNode level3 : level2.get("children")) {
                    for (JsonNode level4 : level3.get("children")) {
                        map.put(level4.get("fund_id").asText(),
                                level4.get("absoluteTicker").asText());
                    }
                }
            }
        }

        return map;
    }


    private String fetchAllFundsHtml() {
        Client client = PipelineHelper.getClient();
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

//        URL url = ClassLoader.getSystemResource("categories.js");
//        String js = FileUtils.readFileToString(new File(url.getFile()));
//
//        js = js.replace("\n", "");
//        Pattern pattern = Pattern.compile("return (\\{.*\\})");
//
//        Matcher matcher = pattern.matcher(js);
//        matcher.find();
//
//        String json = matcher.group(1).replaceAll("s0", "\"s0\"");
//
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//        JsonFactory factory = mapper.getJsonFactory();
//        JsonParser jp = factory.createJsonParser(json);
//        JsonNode root = mapper.readTree(jp);
//
//        Map<String, String> map = new HashMap<String, String>();
//        for (JsonNode level1 : root.get("children")) {
//
//            for (JsonNode level2 : level1.get("children")) {
//
//                for (JsonNode level3 : level2.get("children")) {
//                    for (JsonNode level4 : level3.get("children")) {
//                        map.put(level4.get("fund_id").toString(),
//                                level4.get("absoluteTicker").asText());
//                    }
//                }
//            }
//        }


    }


}


