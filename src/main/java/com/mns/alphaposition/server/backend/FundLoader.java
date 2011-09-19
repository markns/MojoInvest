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
import java.util.List;
import java.util.logging.Logger;

@Singleton
public class FundLoader extends HttpServlet {

    private static final Logger log = Logger.getLogger(FundLoader.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        List<FundLite> fundLites = scrapeFundList();
        if (fundLites != null)
            scrapeFundDetails(fundLites);
    }

    private List<FundLite> scrapeFundList() throws IOException {
        String html = fetch("http://moneycentral.msn.com/investor/partsub/funds/etfperformancetracker.aspx",
                "tab=mkt&show=all");
        List<FundLite> fundLites = null;
        if (html != null) {
            fundLites = scrapeList(html);
            log.warning("Scraped a list of " + fundLites.size() + " funds from msn moneycentral");
        }
        return fundLites;
    }

    private void scrapeFundDetails(List<FundLite> fundLites) throws IOException {
        for (FundLite fundLite : fundLites) {
            if ("TICKER".equals(fundLite.symbol))
                continue;
            log.warning("Loading details for " + fundLite.symbol);
            String html = fetch("http://moneycentral.msn.com/investor/partsub/funds/etfsnapshot.asp",
                    "symbol=" + fundLite.symbol);
            scrapeDetails(html, fundLite);
        }
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

    public void scrapeDetails(String html, FundLite fundLite) throws IOException {
        Document doc = Jsoup.parse(html);
        for (Element element : doc.select("span")) {
            if ("QUICK STATS".equals(element.text())) {
                Element tbody = element.parent().parent().getElementsByTag("tbody").get(0);
                fundLite.category = tbody.child(3).child(1).text();
                fundLite.provider = tbody.child(4).child(1).text();
                fundLite.index = tbody.child(5).child(1).text();
                fundLite.inceptionDate = tbody.child(6).child(1).text();
            }

            if ("OVERVIEW".equals(element.text())) {
                fundLite.overview = element.parent().parent().ownText();
            }
        }
    }

    private String fetch(String url, String query) {
        String charset = "UTF-8";
        try {
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
        } catch (IOException e) {
            log.severe(e.getMessage() + "\n" + e.getCause());
            //TODO: Don't return null
            return null;
        }
    }

    private static class FundLite {
        String name;
        String symbol;
        String provider;
        String category;
        String index;
        String inceptionDate;
        String overview;

        private FundLite(String name, String symbol) {
            this.name = name;
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return name + " " + symbol + " " + provider + " " + category + " " + index + " " + inceptionDate;
        }
    }

}