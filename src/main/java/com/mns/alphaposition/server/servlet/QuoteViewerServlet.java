package com.mns.alphaposition.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.alphaposition.server.engine.model.Quote;
import com.mns.alphaposition.server.engine.model.QuoteDao;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class QuoteViewerServlet extends HttpServlet {

    private final QuoteDao dao;

    @Inject
    public QuoteViewerServlet(QuoteDao dao) {
        this.dao = dao;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        long t = System.currentTimeMillis();

        List<Quote> quotes = dao.list();


        resp.setContentType("text/html");

        resp.getWriter().println("Time taken for query: " + (System.currentTimeMillis() - t));

        resp.getWriter().println("<ul>");
        for (Quote quote : quotes) {
            resp.getWriter().println("<li>" + quote + "</li>");
        }
        resp.getWriter().println("</ul>");
    }
}
