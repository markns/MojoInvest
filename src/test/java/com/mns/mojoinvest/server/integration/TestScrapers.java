package com.mns.mojoinvest.server.integration;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TestScrapers {

    @Ignore
    @Test
    public void testListScrape() throws IOException {
        URL url = ClassLoader.getSystemResource("etfperformancetracker.html");
        String html = FileUtils.readFileToString(new File(url.getFile()));
        Document doc = Jsoup.parse(html);
        Element table = doc.getElementById("ctl00_ctl00_ctl00_ctl00_HtmlBody_HtmlBody_HtmlBody_Column1_dgETF");
        Element tbody = table.getElementsByTag("tbody").get(0);
        Elements tr = tbody.getElementsByTag("tr");

        String name;
        String symbol;
        String category;
        for (Element element : tr) {
            name = ((Element) element.childNode(1)).text();
            symbol = ((Element) element.childNode(2)).text();
            category = ((Element) element.childNode(3)).text();
            System.out.println(name + " " + symbol + " " + category);
        }
    }

    @Test
    public void testSnapshotScrape() throws IOException {
        URL url = ClassLoader.getSystemResource("etfsnapshot.html");
        String html = FileUtils.readFileToString(new File(url.getFile()));
        Document doc = Jsoup.parse(html);

        for (Element element : doc.select("span")) {
            if ("QUICK STATS".equals(element.text())) {
                Element tbody = element.parent().parent().getElementsByTag("tbody").get(0);
                String category = tbody.child(3).child(1).text();
                String provider = tbody.child(4).child(1).text();
                String index = tbody.child(5).child(1).text();
                String inceptionDate = tbody.child(6).child(1).text();
            }

            if ("OVERVIEW".equals(element.text())) {
                String overview = element.parent().parent().ownText();

            }

        }


    }

    @Test
    public void testQuoteScrape() throws IOException {
        URL url = ClassLoader.getSystemResource("etf-quote-msn.html");
        String html = FileUtils.readFileToString(new File(url.getFile()));
        Document doc = Jsoup.parse(html);

        Map<String, String> data = new HashMap<String, String>();
        data.put("close", doc.getElementById("quickquoteb").getElementsByClass("lp").text());
        for (Element span : doc.getElementById("area1").select("span")) {
            if ("DETAILS".equals(span.text())) {
                Element tbody = span.parent().parent().select("tbody").get(0);
                for (Element tr : tbody.select("tr")) {
                    data.put(tr.child(0).text(), tr.child(1).text());
                }

                System.out.println(data);
            }
        }
    }

    protected BigDecimal newBigDecimal(String str) {
        str = str.replaceAll(",", "");
        return new BigDecimal(str);
    }
}
