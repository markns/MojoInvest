package com.mns.mojoinvest.server.servlet;

import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.mns.mojoinvest.server.pipeline.DailyPipeline;
import com.mns.mojoinvest.server.servlet.util.ParameterParser;
import org.joda.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class PipelineServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(PipelineServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ParameterParser parser = new ParameterParser(req);
        LocalDate date = parser.getLocalDateParameter("date", new LocalDate());

        PipelineService service = PipelineServiceFactory.newPipelineService();
        String pipelineId = service.startNewPipeline(new DailyPipeline(), date);

        log.info("Daily pipeline '" + pipelineId + "' started for date " + date);
    }
}
