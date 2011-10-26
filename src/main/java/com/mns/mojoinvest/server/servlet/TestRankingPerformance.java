package com.mns.mojoinvest.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.Ranking;
import com.mns.mojoinvest.server.engine.model.RankingDao;
import com.mns.mojoinvest.server.engine.model.RankingText;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.joda.time.LocalDate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Singleton
public class TestRankingPerformance extends HttpServlet {

    public static final String COPY = "copy";
    private final RankingDao dao;

    @Inject
    public TestRankingPerformance(RankingDao dao) {
        this.dao = dao;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        if (req.getParameter(COPY) != null) {
            long t = System.currentTimeMillis();
            List<Ranking> rankings = dao.list();
            resp.setContentType("text/html");
            resp.getWriter().println("Time taken for query: " + (System.currentTimeMillis() - t));
            resp.getWriter().println("<ul>");
            List<RankingText> rankingTexts = new ArrayList<RankingText>(rankings.size());
            for (Ranking ranking : rankings) {
                rankingTexts.add(new RankingText(ranking.getDate(), b(ranking.m9()), b(ranking.m9())));
                resp.getWriter().println("<li>" + ranking + "</li>");
            }
            resp.getWriter().println("</ul>");
            t = System.currentTimeMillis();
            dao.put(rankingTexts);
            resp.getWriter().println("Time taken for put: " + (System.currentTimeMillis() - t));
        }
        LocalDate from = new LocalDate("2002-05-22");
        LocalDate to = new LocalDate("2011-09-16");
        List<LocalDate> dates = TradingDayUtils.getDailySeries(from, to, true);

        resp.setContentType("text/html");
        resp.getWriter().println("<ul>");
        long x = System.currentTimeMillis();
//        Collection<Ranking> rankings = dao.get(dates);
//        resp.getWriter().println("<li>" + "Time taken to get " + rankings.size() + " rankings: " + (System.currentTimeMillis() - x) + "</li>");
//
//        x = System.currentTimeMillis();
//        Collection<RankingText> rankingTexts = dao.getRankingText(dates);
//        resp.getWriter().println("<li>" + "Time taken to get " + rankingTexts.size() + " rankingTexts: " + (System.currentTimeMillis() - x) + "</li>");

        x = System.currentTimeMillis();
        Collection<Ranking> rankings = dao.get(dates.subList(dates.size() - 121, dates.size() - 1));
        resp.getWriter().println("<li>" + "Time taken to get " + rankings.size() + " rankings: " + (System.currentTimeMillis() - x) + "</li>");

        x = System.currentTimeMillis();
        Collection<RankingText> rankingTexts = dao.getRankingText(dates.subList(dates.size() - 121, dates.size() - 1));
        resp.getWriter().println("<li>" + "Time taken to get " + rankingTexts.size() + " rankingTexts: " + (System.currentTimeMillis() - x) + "</li>");

    }

    private String b(String s) {
        return s + "," + s + "," + s + "," + s + "," + s + "," + s + "," + s + "," + s + "," + s + "," + s + "," +
                s + "," + s + "," + s + "," + s + "," + s + "," + s + "," + s + "," + s + "," + s + "," + s;
    }


}
