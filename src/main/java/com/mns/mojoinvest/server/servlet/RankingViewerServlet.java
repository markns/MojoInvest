package com.mns.mojoinvest.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.Ranking;
import com.mns.mojoinvest.server.engine.model.RankingParams;
import com.mns.mojoinvest.server.engine.model.dao.RankingDao;
import com.mns.mojoinvest.server.servlet.util.ParameterNotFoundException;
import com.mns.mojoinvest.server.servlet.util.ParameterParser;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.joda.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@Singleton
public class RankingViewerServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(RankingViewerServlet.class.getName());

    private final RankingDao dao;

    @Inject
    public RankingViewerServlet(RankingDao dao) {
        this.dao = dao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ParameterParser parser = new ParameterParser(req);

        int formationPeriod = 0;
        LocalDate from;
        try {
            formationPeriod = parser.getIntParameter("formation");
            from = parser.getLocalDateParameter("from");
        } catch (ParameterNotFoundException e) {
            throw new ServletException(e);
        }

        LocalDate to = parser.getLocalDateParameter("to", new LocalDate());
        List<LocalDate> dates = TradingDayUtils.getDailySeries(from, to, true);

        Collection<Ranking> rankings = dao.get(dates, new RankingParams(formationPeriod));

        resp.setContentType("text/html");

        resp.getWriter().println("<ul>");
        for (Ranking ranking : rankings) {
            resp.getWriter().println("<li>" + ranking + "</li>");
        }
        resp.getWriter().println("</ul>");
    }
}
