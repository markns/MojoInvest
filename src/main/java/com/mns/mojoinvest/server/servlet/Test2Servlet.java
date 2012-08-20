package com.mns.mojoinvest.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.DataAccessException;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.servlet.util.ParameterNotFoundException;
import com.mns.mojoinvest.server.servlet.util.ParameterParser;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.joda.time.LocalDate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class Test2Servlet extends HttpServlet {

    private final QuoteDao dao;

    @Inject
    public Test2Servlet(QuoteDao dao) {
        this.dao = dao;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        List<LocalDate> dates = TradingDayUtils.getDailySeries
                (new LocalDate("2010-01-01"), new LocalDate("2012-01-01"), true);

        ParameterParser parser = new ParameterParser(req);

        String symbol = null;
        try {
            symbol = parser.getStringParameter("symbol");
        } catch (ParameterNotFoundException e) {
            //pass
        }


        resp.setContentType("text/html");
        resp.getWriter().println("<ul>");
        for (LocalDate date : dates) {
            Quote quote = null;
            try {
                quote = dao.get(symbol, date);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (DataAccessException e) {
                e.printStackTrace();
            }
            resp.getWriter().println("<li>" + date + " - " + quote + "</li>");
        }
        resp.getWriter().println("</ul>");
    }
}

