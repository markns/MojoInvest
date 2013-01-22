package com.mns.mojoinvest.server.pipeline.fund;

import com.mns.mojoinvest.server.engine.model.Fund;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ISharesFundDetailFetcherBatchJobTest {

    ISharesFundDetailFetcherBatchJob job = new ISharesFundDetailFetcherBatchJob();

    @Test
    public void testScrapeDetails() throws IOException {

        URL url = ClassLoader.getSystemResource("IAEX.html");
        String html = FileUtils.readFileToString(new File(url.getFile()));
        Fund fund = job.buildFund(html);
        System.out.println(fund);
    }

    @Test
    public void testScrapeDetails2() throws IOException {

        URL url = ClassLoader.getSystemResource("ishares_idvy_detail.html");
        String html = FileUtils.readFileToString(new File(url.getFile()));
        Fund fund = job.buildFund(html);
        System.out.println(fund);

    }


    @Test
    public void testParseCategoryJs() throws IOException {

        URL url = ClassLoader.getSystemResource("ishares_product_overview.html");


        String html = FileUtils.readFileToString(new File(url.getFile()));

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
        JsonParser jp = factory.createJsonParser(json);
        JsonNode root = mapper.readTree(jp);

        Map<String, String> map = new HashMap<String, String>();
        for (JsonNode level1 : root.get("children")) {

            for (JsonNode level2 : level1.get("children")) {

                for (JsonNode level3 : level2.get("children")) {
                    for (JsonNode level4 : level3.get("children")) {
                        map.put(level4.get("fund_id").toString(),
                                level4.get("absoluteTicker").asText());
                    }
                }
            }
        }

        for (String s : map.keySet()) {
            System.out.println(s + ",");
        }

    }


}
