package com.mns.mojoinvest.server.pipeline.fund;

import com.mns.mojoinvest.server.engine.model.Fund;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ISharesFundDetailFetcherBatchJobTest {

    ISharesFundDetailFetcherBatchJob job = new ISharesFundDetailFetcherBatchJob();

    @Test
    public void testScrapeDetails() throws IOException {

        URL url = ClassLoader.getSystemResource("ishares_iaex_detail.html");
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

        URL url = ClassLoader.getSystemResource("categories.js");
        String js = FileUtils.readFileToString(new File(url.getFile()));

        Pattern pattern = Pattern.compile("return (\\{.+\\})");

        Matcher matcher = pattern.matcher(js);
        matcher.find();

        String json = matcher.group(1).replaceAll("s0", "\"s0\"");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        JsonFactory factory = mapper.getJsonFactory();
        JsonParser jp = factory.createJsonParser(json);
        JsonNode root = mapper.readTree(jp);

        System.out.println(root);
        for (JsonNode node1 : root.get("children")) {
            System.out.println(node1);
        }


//        System.out.println(node);
    }


}
