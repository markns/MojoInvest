package com.mns.alphaposition.server.backend;

import com.google.inject.Singleton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.logging.Logger;

@Singleton
public class FundLoader extends HttpServlet {

    private static final Logger log = Logger.getLogger(FundLoader.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            String html = fetch();
            Document doc = Jsoup.parse(html);
        } catch (Exception e) {
            log.severe("Unable to load funds at " + new Date() + ". " + e.getMessage());
        }


    }

    private String fetch() throws Exception {
        String url = "http://moneycentral.msn.com/investor/partsub/funds/etfperformancetracker.aspx";
        String charset = "UTF-8";
//        String query = "tab=mkt&show=all";
        String query = "tab=mkt&p=0";

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

}