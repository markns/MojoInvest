package com.mns.alphaposition.server.integration;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TestScrapers {

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


}
