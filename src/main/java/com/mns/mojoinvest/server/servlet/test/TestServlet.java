package com.mns.mojoinvest.server.servlet.test;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.dao.DataAccessException;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import org.joda.time.LocalDate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class TestServlet extends HttpServlet {

    private final QuoteDao dao;

    @Inject
    public TestServlet(QuoteDao dao) {
        this.dao = dao;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            dao.get("ITWN", new LocalDate(2013, 1, 4));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

    }
}

