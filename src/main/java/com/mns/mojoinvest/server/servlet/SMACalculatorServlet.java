package com.mns.mojoinvest.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.calculator.CalculationService;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.util.QuoteUtils;
import org.joda.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class SMACalculatorServlet extends HttpServlet {

    private final QuoteDao quoteDao;
    private final FundDao fundDao;

    @Inject
    public SMACalculatorServlet(QuoteDao quoteDao, FundDao fundDao) {
        this.quoteDao = quoteDao;
        this.fundDao = fundDao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        CalculationService service = new CalculationService(quoteDao);

        for (Fund fund : fundDao.getAll()) {

            List<Quote> quotes = quoteDao.query(fund);
            QuoteUtils.sortByDateAsc(quotes);
            LocalDate earliest = quotes.get(0).getDate();
            LocalDate latest = quotes.get(quotes.size() - 1).getDate();

            System.out.println(fund + " " + earliest + " " + latest);

            int period = 4;

            System.out.println("*****");
            service.calculateSMA(fund, earliest, latest, period);
            System.out.println("*****");
            service.calculateSMA(fund, earliest, latest.minusDays(1), period);
            System.out.println("*****");
            service.calculateSMA(fund, earliest, latest.minusDays(2), period);
            System.out.println("*****");
            service.calculateSMA(fund, earliest, latest.minusDays(3), period);
            System.out.println("*****");
            service.calculateSMA(fund, earliest, latest.minusDays(4), period);

            break;
        }


    }


}
