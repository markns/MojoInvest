package com.mns.alphaposition.server.backend;

import com.google.inject.Singleton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Singleton
public class FundLoader extends HttpServlet {

    private static final Logger log = Logger.getLogger(FundLoader.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            String html = fetch("http://moneycentral.msn.com/investor/partsub/funds/etfperformancetracker.aspx", "tab=mkt&p=0");
            List<FundLite> fundLites = scrapeList(html);
            for (FundLite fundLite : fundLites) {
                html = fetch("http://moneycentral.msn.com/investor/partsub/funds/etfsnapshot.asp", "symbol=" + fundLite.symbol);
                FundDetails details = scrapeDetails(html);
                buildFund(fundLite, details);
            }

        } catch (Exception e) {
            log.severe("Unable to load funds at " + new Date() + ". " + e.getMessage());
        }

    }

    private void buildFund(FundLite fundLite, FundDetails details) {
        log.severe(fundLite + " " + details);
    }

    private String fetch(String url, String query) throws Exception {
        String charset = "UTF-8";
        URLConnection connection = new URL(url + "?" + query).openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setReadTimeout(10);
        InputStream response = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    public List<FundLite> scrapeList(String html) throws IOException {
        Document doc = Jsoup.parse(html);
        Element table = doc.getElementById("ctl00_ctl00_ctl00_ctl00_HtmlBody_HtmlBody_HtmlBody_Column1_dgETF");
        Element tbody = table.getElementsByTag("tbody").get(0);
        Elements tr = tbody.getElementsByTag("tr");
        List<FundLite> data = new ArrayList<FundLite>();
        for (Element element : tr) {
            String name = ((Element) element.childNode(1)).text();
            String symbol = ((Element) element.childNode(2)).text();
            FundLite p = new FundLite(name, symbol);
            data.add(p);
        }
        return data;
    }

    public FundDetails scrapeDetails(String html) throws IOException {
        Document doc = Jsoup.parse(html);
        Element area1 = doc.getElementById("area1");

        Element tbody = area1.child(0).getElementsByTag("tbody").get(0);

        if (!"OVERVIEW".equals(area1.child(1).child(0).text())) {
            throw new IllegalStateException("Source has changed");
        }

        String category = tbody.child(3).child(1).text();
        String provider = tbody.child(4).child(1).text();
        String index = tbody.child(5).child(1).text();
        String inceptionDate = tbody.child(6).child(1).text();
        String overview = area1.child(1).childNode(2).toString();

        return new FundDetails(category, provider, index, inceptionDate, overview);
    }

    private static class FundLite {
        String name;
        String symbol;
        private FundLite(String name, String symbol) {
            this.name = name;
            this.symbol = symbol;
        }
        @Override
        public String toString() {
            return name + " " + symbol;
        }
    }

    private static class FundDetails {
        String category;
        String provider;
        String index;
        String inceptionDate;
        String overview;
        public FundDetails(String category, String provider, String index, String inceptionDate, String overview) {
            this.category = category;
            this.provider = provider;
            this.index = index;
            this.inceptionDate = inceptionDate;
            this.overview = overview;
        }
        @Override
        public String toString() {
            return category + " " + provider + " " + index + " " + inceptionDate + " " + overview;
        }
    }

}