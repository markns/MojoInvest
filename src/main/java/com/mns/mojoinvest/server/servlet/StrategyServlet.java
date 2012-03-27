package com.mns.mojoinvest.server.servlet;

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
import org.joda.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

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

        Date fromDate = new LocalDate("2000-01-01").toDateMidnight().toDate();
        Date toDate = new LocalDate("2012-03-22").toDateMidnight().toDate();

        double cash = parser.getDoubleParameter("cash", 10000d);
        double transactionCost = parser.getDoubleParameter("txcost", 10d);
        int portfolioSize = parser.getIntParameter("portfolio", 3);
        int holdingPeriod = parser.getIntParameter("holding", 1);
        int ma1 = parser.getIntParameter("ma1", 12);
        int ma2 = parser.getIntParameter("ma2", 26);
        int castOff = parser.getIntParameter("castoff", 8);
        int stddev = parser.getIntParameter("stddev", 26);
        boolean equityCurveTrading = parser.getBooleanParameter("equitycurve", true);
        int equityCurveWindow = parser.getIntParameter("ecwindow", 50);

        Portfolio portfolio = portfolioFactory.create(new PortfolioParams(cash, transactionCost, fromDate));

        BacktestParams params = new BacktestParams(fromDate, toDate);

        //4, 12, 26, 40, 52
        Strategy2Params strategyParams = new Strategy2Params(portfolioSize, holdingPeriod, ma1, ma2,
                castOff, stddev, equityCurveTrading, equityCurveWindow);

        Collection<Fund> universe = fundDao.getAll();

        try {
            strategy2.execute(portfolio, params, universe, strategyParams);
        } catch (StrategyException e) {
            e.printStackTrace();
        }

        super.doGet(req, resp);
    }

    public static class Strategy2Params {
        private final int portfolioSize;
        private final int rebalanceFrequency;
        private final int ma1;
        private final int ma2;
        private final int castOff;
        private final int stddev;
        private final boolean equityCurveTrading;
        private final int equityCurveWindow;

        private Strategy2Params(int portfolioSize, int rebalanceFrequency, int ma1, int ma2, int castOff, int stddev, boolean equityCurveTrading, int equityCurveWindow) {

            this.portfolioSize = portfolioSize;
            this.rebalanceFrequency = rebalanceFrequency;
            this.ma1 = ma1;
            this.ma2 = ma2;
            this.castOff = castOff;
            this.stddev = stddev;
            this.equityCurveTrading = equityCurveTrading;
            this.equityCurveWindow = equityCurveWindow;
        }

        public int getPortfolioSize() {
            return portfolioSize;
        }

        public int getRebalanceFrequency() {
            return rebalanceFrequency;
        }

        public int getMa1() {
            return ma1;
        }

        public int getMa2() {
            return ma2;
        }

        public int getCastOff() {
            return castOff;
        }

        public int getStdDev() {
            return stddev;
        }

        public boolean isEquityCurveTrading() {
            return equityCurveTrading;
        }

        public int getEquityCurveWindow() {
            return equityCurveWindow;
        }
    }
}
