package com.mns.alphaposition.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.alphaposition.server.engine.model.Ranking;
import com.mns.alphaposition.server.engine.model.RankingDao;
import com.mns.alphaposition.server.engine.model.RankingList;
import com.mns.alphaposition.server.util.TradingDayUtils;
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
            List<RankingList> rankingLists = new ArrayList<RankingList>(rankings.size());
            for (Ranking ranking : rankings) {
                rankingLists.add(new RankingList(ranking.getDate(), ranking.getM9()));
                resp.getWriter().println("<li>" + ranking + "</li>");
            }
            resp.getWriter().println("</ul>");
            t = System.currentTimeMillis();
            dao.put(rankingLists);
            resp.getWriter().println("Time taken for put: " + (System.currentTimeMillis() - t));
        }
        LocalDate from = new LocalDate("2002-05-22");
        LocalDate to = new LocalDate("2011-09-16");
        List<LocalDate> dates = TradingDayUtils.getDailySeries(from, to, true);

        long x = System.currentTimeMillis();
        Collection<Ranking> rankings = dao.get(dates);
        resp.getWriter().println("Time taken to get " + rankings.size() + " rankings: " + (System.currentTimeMillis() - x));

        x = System.currentTimeMillis();
        Collection<RankingList> rankingLists = dao.getLists(dates);
        resp.getWriter().println("Time taken to get " + rankingLists.size() + " rankingLists: " + (System.currentTimeMillis() - x));

    }

}
