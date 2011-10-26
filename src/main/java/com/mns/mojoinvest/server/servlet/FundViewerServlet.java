package com.mns.mojoinvest.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.FundDao;

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

    private final FundDao dao;

    @Inject
    public FundViewerServlet(FundDao dao) {
        this.dao = dao;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        long t = System.currentTimeMillis();

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("provider", "PowerShares");
        List<Fund> funds = dao.query(queryParams);

        resp.setContentType("text/html");

        resp.getWriter().println("Time taken for query: " + (System.currentTimeMillis() - t));

        resp.getWriter().println("<ul>");
        for (Fund fund : funds) {
            resp.getWriter().println("<li>" + fund + "</li>");
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
