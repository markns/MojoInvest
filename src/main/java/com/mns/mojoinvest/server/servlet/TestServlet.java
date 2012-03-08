package com.mns.mojoinvest.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import org.joda.time.LocalDate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class TestServlet extends HttpServlet {

    private final QuoteDao dao;
    private final ObjectifyFactory factory;

    @Inject
    public TestServlet(QuoteDao dao, final ObjectifyFactory objectifyFactory) {
        this.dao = dao;
        this.factory = objectifyFactory;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {


        Quote quote2 = dao.get("UCO", new LocalDate("2011-02-14"));
        Quote quote3 = null;
        try {
            quote3 = dao.get("UCO", new LocalDate("2011-04-14"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Quote quote4 = dao.query("UCO", new LocalDate("2011-04-14")).get(0);

        resp.setContentType("text/html");
        resp.getWriter().println("<ul>");
        resp.getWriter().println("<li>" + quote2 + ", Key: " +
                factory.getMetadataForEntity(quote2).getRawKey(quote2) + ", Entity: " +
                factory.getMetadataForEntity(quote2).toEntity(quote2, factory.begin()).toString() + "</li>");
        resp.getWriter().println("<li>" + quote3 + "</li>");
//                + ", Key: " +
//                factory.getMetadataForEntity(quote3).getRawKey(quote3) + ", Entity: " +
//                factory.getMetadataForEntity(quote3).toEntity(quote3, factory.begin()).toString() + "</li>");
        resp.getWriter().println("<li>" + quote4 + ", Key: " +
                factory.getMetadataForEntity(quote4).getRawKey(quote4) + ", Entity: " +
                factory.getMetadataForEntity(quote4).toEntity(quote4, factory.begin()).toString() + "</li>");
        resp.getWriter().println("</ul>");
    }
}

