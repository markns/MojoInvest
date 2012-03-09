package com.mns.mojoinvest.server.servlet;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Singleton
public class FundViewerServlet extends HttpServlet {
    public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    private final FundDao dao;

    @Inject
    public FundViewerServlet(FundDao dao) {
        this.dao = dao;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        boolean csv = Boolean.parseBoolean(req.getParameter("csv"));

        Collection<Fund> funds = dao.getAll();

        resp.setContentType("text/html");

        resp.getWriter().println("<ul>");
        CSVWriter writer = new CSVWriter(resp.getWriter());

        if (csv) {
            for (Fund fund : funds) {
                resp.getWriter().write("<li>");

                String date = "";
                if (fund.getInceptionDate() != null) {
                    date = fmt.print(fund.getInceptionDate());
                }
                String[] data = new String[]{fund.getSymbol(),
                        fund.getProvider(),
                        fund.getCategory(),
                        date
                };
                writer.writeNext(data);
                resp.getWriter().write("</li>");

            }
        } else {
            for (Fund fund : funds) {
                resp.getWriter().println("<li>" + fund + "</li>");
            }
        }

        resp.getWriter().write("<p>Categories: " + dao.getCategorySet().size() + "</p>");
        resp.getWriter().write("<p>Categories: " + dao.getCategorySet() + "</p>");
        resp.getWriter().write("<p>Providers: " + dao.getProviderSet().size() + "</p>");
        resp.getWriter().write("<p>Providers: " + dao.getProviderSet() + "</p>");

        resp.getWriter().println("</ul>");
    }

}
