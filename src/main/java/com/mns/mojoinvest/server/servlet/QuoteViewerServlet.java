package com.mns.mojoinvest.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDaoImpl;
import com.mns.mojoinvest.server.servlet.util.ParameterNotFoundException;
import com.mns.mojoinvest.server.servlet.util.ParameterParser;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class QuoteViewerServlet extends HttpServlet {

    private final QuoteDaoImpl dao;

    @Inject
    public QuoteViewerServlet(QuoteDaoImpl dao) {
        this.dao = dao;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        ParameterParser parser = new ParameterParser(req);

        List<Quote> quotes;
        try {
            String symbol = parser.getStringParameter("symbol");
            quotes = dao.query(symbol);
        } catch (ParameterNotFoundException e) {
            quotes = dao.list();
        }

        resp.setContentType("text/html");
        resp.getWriter().println("<ul>");
        for (Quote quote : quotes) {
            resp.getWriter().println("<li>" + quote + ", rolled=" + quote.isRolled() + "</li>");
        }
        resp.getWriter().println("</ul>");
    }
}
