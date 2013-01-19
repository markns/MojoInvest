package com.mns.mojoinvest.server.pipeline.servlet;

import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.OrphanedObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.servlet.params.ParameterNotFoundException;
import com.mns.mojoinvest.server.servlet.params.ParameterParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Singleton
public class SubmitSessionId extends HttpServlet {

    private static final Logger log = Logger.getLogger(SubmitSessionId.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ParameterParser parser = new ParameterParser(req);
        try {
            String promiseHandle = parser.getStringParameter("ph");

            resp.setContentType("text/html");

            resp.getWriter().println("Get jsessionid from <a href=\"http://uk.ishares.com/en/rc/tools/performance-chart\">here</a>");
            resp.getWriter().println("<form action=\"/tools/isession\" method=\"post\">\n" +
                    "<input type=\"hidden\" name=\"ph\" value=\"" + promiseHandle + "\"><br>\n" +
                    "Session id: <input type=\"text\" name=\"sessionid\"><br>\n" +
                    "<input type=\"submit\" value=\"Submit\">\n" +
                    "</form>\n");

        } catch (ParameterNotFoundException e) {
            log.throwing(this.getClass().getSimpleName(), "doGet", e);
            throw new ServletException(e);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PipelineService service = PipelineServiceFactory.newPipelineService();
        String sessionid = req.getParameter("sessionid");
        String ph = req.getParameter("ph");
        try {
            log.info("Submitting promised value: " + sessionid);
            service.submitPromisedValue(ph, sessionid);
        } catch (OrphanedObjectException e) {
            throw new ServletException(e);
        } catch (NoSuchObjectException e) {
            throw new ServletException(e);
        }

    }
}
