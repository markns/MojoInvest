package com.mns.mojoinvest.server.servlet;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioFactory;
import com.mns.mojoinvest.server.engine.result.StrategyResultBuilder;
import com.mns.mojoinvest.server.engine.strategy.MomentumStrategy2;
import com.mns.mojoinvest.server.engine.strategy.StrategyException;
import com.mns.mojoinvest.server.servlet.util.ParameterParser;
import com.mns.mojoinvest.shared.params.BacktestParams;
import com.mns.mojoinvest.shared.params.PortfolioParams;
import com.mns.mojoinvest.shared.params.Strategy2Params;
import org.joda.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Singleton
public class StrategyServlet extends HttpServlet {

    private final MomentumStrategy2 strategy2;

    private final PortfolioFactory portfolioFactory;
    private final StrategyResultBuilder strategyResultBuilder;
    private final FundDao fundDao;

    @Inject
    public StrategyServlet(MomentumStrategy2 strategy2, PortfolioFactory portfolioFactory,
                           StrategyResultBuilder strategyResultBuilder, FundDao fundDao) {
        this.strategy2 = strategy2;
        this.portfolioFactory = portfolioFactory;
        this.strategyResultBuilder = strategyResultBuilder;
        this.fundDao = fundDao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ParameterParser parser = new ParameterParser(req);

        //TODO: java.lang.IndexOutOfBoundsException when start date set to 1996-04-01

        LocalDate fDate = parser.getLocalDateParameter("from", new LocalDate("2000-01-01"));
        LocalDate tDate = parser.getLocalDateParameter("to", new LocalDate("2012-03-22"));
        Date fromDate = fDate.toDateMidnight().toDate();
        Date toDate = tDate.toDateMidnight().toDate();

        double cash = parser.getDoubleParameter("cash", 10000d);
        double transactionCost = parser.getDoubleParameter("txcost", 10d);
        int portfolioSize = parser.getIntParameter("portfolio", 3);
        int holdingPeriod = parser.getIntParameter("holding", 1);
        int ma1 = parser.getIntParameter("ma1", 12);
        int ma2 = parser.getIntParameter("ma2", 26);
        int roc = parser.getIntParameter("roc", 26);
        int castOff = parser.getIntParameter("castoff", 5);
        int stddev = parser.getIntParameter("stddev", 26);
        boolean equityCurveTrading = parser.getBooleanParameter("equitycurve", true);
        int equityCurveWindow = parser.getIntParameter("ecwindow", 60);
        String relativeStrengthStyle = parser.getStringParameter("rsstyle", "MA");

        String funds = parser.getStringParameter("funds", null);
        Collection<Fund> universe;
        if (funds != null) {
            universe = fundDao.get(toList(Splitter.on("|").split(funds)));
        } else {
            universe = fundDao.getAll();
        }
        Portfolio portfolio = portfolioFactory.create(new PortfolioParams(cash, transactionCost, fromDate));

        BacktestParams params = new BacktestParams(fromDate, toDate);

        //4, 12, 26, 40, 52
        Strategy2Params strategyParams = new Strategy2Params(portfolioSize, holdingPeriod, ma1, ma2, roc,
                castOff, stddev, equityCurveTrading, equityCurveWindow, relativeStrengthStyle);


        try {
            strategy2.execute(portfolio, params, universe, strategyParams);
        } catch (StrategyException e) {
            e.printStackTrace();
        }

        super.doGet(req, resp);
    }

    private static <E> List<E> toList(Iterable<E> iterable) {
        return (iterable instanceof List)
                ? (List<E>) iterable
                : Lists.newArrayList(iterable.iterator());
    }

}
