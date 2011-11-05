package com.mns.mojoinvest.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.util.HttpUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Singleton
public class FundLoaderServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(FundLoaderServlet.class.getName());

    private FundDao dao;

    @Inject
    public FundLoaderServlet(FundDao dao) {
        this.dao = dao;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        List<FundLite> fundLites = scrapeFundList();
        if (fundLites != null)
            scrapeFundDetails(fundLites);
        List<Fund> funds = buildFunds(fundLites);
        log.info("Saving " + funds.size() + " funds");
        dao.put(funds);
    }

    private List<Fund> buildFunds(List<FundLite> fundLites) {
        List<Fund> funds = new ArrayList<Fund>();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
        for (FundLite fundLite : fundLites) {
            try {
                //TODO: Add more validation to the fields here. Only create a fund if it has good data
                Fund fund = new Fund(fundLite.symbol, fundLite.name, fundLite.category, fundLite.provider, true, "US",
                        fundLite.index, fundLite.overview, fmt.parseDateTime(fundLite.inceptionDate).toLocalDate());
                funds.add(fund);
            } catch (Exception e) {
                log.severe("Exception while creating fund with fields: " + fundLite.symbol + " " +
                        fundLite.name + " " + fundLite.inceptionDate);
            }
        }
        return funds;
    }

    public List<FundLite> scrapeFundList() throws IOException {
        String html = HttpUtils.fetch("http://moneycentral.msn.com/investor/partsub/funds/etfperformancetracker.aspx",
                "tab=mkt&show=all");
        List<FundLite> fundLites = null;
        if (html != null) {
            fundLites = scrapeList(html);
            log.info("Scraped a list of " + fundLites.size() + " funds from msn moneycentral");
        }
        return fundLites;
    }

    private void scrapeFundDetails(List<FundLite> fundLites) throws IOException {
        for (FundLite fundLite : fundLites) {
            if ("TICKER".equals(fundLite.symbol))
                continue;
            String html = HttpUtils.fetch("http://moneycentral.msn.com/investor/partsub/funds/etfsnapshot.asp",
                    "symbol=" + fundLite.symbol);
            scrapeDetails(html, fundLite);
        }
    }

    private List<FundLite> scrapeList(String html) throws IOException {
        Document doc = Jsoup.parse(html);
        Element table = doc.getElementById("ctl00_ctl00_ctl00_ctl00_HtmlBody_HtmlBody_HtmlBody_Column1_dgETF");
        Element tbody = table.getElementsByTag("tbody").get(0);
        Elements tr = tbody.getElementsByTag("tr");
        List<FundLite> data = new ArrayList<FundLite>();
        for (Element element : tr) {
            String name = ((Element) element.childNode(1)).text();
            String symbol = ((Element) element.childNode(2)).text();
            if ("TICKER".equals(symbol))
                continue;
            FundLite p = new FundLite(name, symbol);
            data.add(p);
        }

        return data;
    }

    private void scrapeDetails(String html, FundLite fundLite) throws IOException {
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



    public static class FundLite {
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

        public String getSymbol() {
            return symbol;
        }
        @Override
        public String toString() {
            return name + " " + symbol + " " + provider + " " + category + " " + index + " " + inceptionDate;
        }
    }

}