package com.mns.mojoinvest.server.servlet.viewer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.joda.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class CalculatedValueViewerServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(CalculatedValueViewerServlet.class.getName());

    private final CalculatedValueDao cvDao;
    private final FundDao fundDao;

    @Inject
    public CalculatedValueViewerServlet(CalculatedValueDao cvDao, FundDao fundDao) {
        this.cvDao = cvDao;
        this.fundDao = fundDao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<LocalDate> dates = TradingDayUtils.getEndOfWeekSeries(new LocalDate("2000-01-01"),
                new LocalDate("2012-03-01"), 1);

        Collection<Fund> funds = fundDao.list();

        long start = System.currentTimeMillis();
        log.fine("Attempting to retrieve " + dates.size() * funds.size() + " cvs");
        Map<String, Map<String, CalculatedValue>> cvs = cvDao.get(funds, "SMA", 12);
        log.fine("Loading " + cvs.size() + " cvs took " + (System.currentTimeMillis() - start) + " ms");

//        resp.setContentType("text/html");
//        resp.getWriter().println("<ul>");
//        for (CalculatedValue cv : cvs) {
//            resp.getWriter().println("<li>" + cv + "</li>");
//        }
//        resp.getWriter().println("</ul>");
    }
}
