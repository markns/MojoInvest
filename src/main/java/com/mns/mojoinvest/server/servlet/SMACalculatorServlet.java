package com.mns.mojoinvest.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.calculator.CalculationService;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.util.QuoteUtils;
import org.joda.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Singleton
public class SMACalculatorServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(SMACalculatorServlet.class.getName());

    private final QuoteDao quoteDao;
    private final FundDao fundDao;
    private final CalculatedValueDao calculatedValueDao;

    @Inject
    public SMACalculatorServlet(QuoteDao quoteDao, FundDao fundDao, CalculatedValueDao calculatedValueDao) {
        this.quoteDao = quoteDao;
        this.fundDao = fundDao;
        this.calculatedValueDao = calculatedValueDao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        CalculationService service = new CalculationService(quoteDao);

        for (Fund fund : fundDao.getAll()) {

            log.info("Running calculations for " + fund);
            List<Quote> quotes = quoteDao.query(fund.getSymbol());
            QuoteUtils.sortByDateAsc(quotes);
            LocalDate earliest = quotes.get(0).getDate();
            LocalDate latest = quotes.get(quotes.size() - 1).getDate();

            List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
            for (int period : Arrays.asList(4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52)) {
                cvs.addAll(service.calculateSMA(fund, earliest, latest, period));
                cvs.addAll(service.calculateSMA(fund, earliest, latest.minusDays(1), period));
                cvs.addAll(service.calculateSMA(fund, earliest, latest.minusDays(2), period));
                cvs.addAll(service.calculateSMA(fund, earliest, latest.minusDays(3), period));
                cvs.addAll(service.calculateSMA(fund, earliest, latest.minusDays(4), period));
            }
            calculatedValueDao.put(cvs);
        }


    }


}
