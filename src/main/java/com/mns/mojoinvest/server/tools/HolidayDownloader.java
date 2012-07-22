package com.mns.mojoinvest.server.tools;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HolidayDownloader {


    public static void main(String[] args) {

//        http://www.bankholidaydates.co.uk/GreatBritain/2012.aspx

        for (String year : new String[]{"2004", "2005", "2006", "2007", "2008", "2009",
                "2011", "2012", "2013", "2014",}) {
            String html = fetchHolidayHtml(year);
            parseAndPrintHolidays(html);
        }

    }

    private static String fetchHolidayHtml(String year) {
        Client c = Client.create();
        WebResource r;
        c.setReadTimeout(10000);
        c.setConnectTimeout(10000);

        r = c.resource("http://www.bankholidaydates.co.uk/GreatBritain/" + year + ".aspx");
        return r.get(String.class);
    }

    private static void parseAndPrintHolidays(String html) {
        Document doc = Jsoup.parse(html);
        Element table = doc.getElementById("holidayListBody");

        for (Element row : table.getElementsByTag("tr")) {

            if ("th".equals(row.child(0).tagName()))
                continue;

            if (row.getElementsByClass("bankHoliday").size() > 0) {
                String date = row.child(0).text();
                String[] dateArr = date.split("/");
                String holiday = row.child(1).text();
                System.out.printf("aMap.put(new LocalDate(\"%s-%s-%s\"), \"%s\");\n", dateArr[2], dateArr[1], dateArr[0], holiday);
            }

        }
    }
}
