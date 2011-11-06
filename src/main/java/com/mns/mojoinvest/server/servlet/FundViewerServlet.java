package com.mns.mojoinvest.server.servlet;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<Fund> funds = dao.list();

        resp.setContentType("text/html");

        resp.getWriter().println("<ul>");
        CSVWriter writer = new CSVWriter(resp.getWriter());

        if (csv) {
            for (Fund fund : funds) {
                resp.getWriter().write("<li>");
                String[] data = new String[]{fund.getSymbol(),
                        fund.getProvider(),
                        fund.getCategory(),
                        fmt.print(fund.getInceptionDate())};
                writer.writeNext(data);
                resp.getWriter().write("</li>");

            }
        } else {
            for (Fund fund : funds) {
                resp.getWriter().println("<li>" + fund + "</li>");
            }
        }
        resp.getWriter().println("</ul>");
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String DATA = request.getParameter("DATA");

        List<Fund> funds = null;

        long t = System.currentTimeMillis();
        if (DATA != null && !DATA.isEmpty()) {
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("provider", DATA);
            funds = dao.query(queryParams);
        } else {
            funds = dao.list();
        }

        out.println("Time taken for query: " + (System.currentTimeMillis() - t));

        out.println("<ul>");
        for (Fund fund : funds) {
            out.println("<li>" + fund + "</li>");
        }
        out.println("</ul>");

        out.close();
    }

}
