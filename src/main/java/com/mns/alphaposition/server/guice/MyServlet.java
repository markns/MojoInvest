package com.mns.alphaposition.server.guice;

import com.google.inject.Inject;
import com.mns.alphaposition.server.engine.strategy.TradingStrategy;
import com.mns.alphaposition.shared.engine.model.Fund;
import com.mns.alphaposition.shared.params.MomentumStrategyParams;
import com.mns.alphaposition.shared.params.SimpleRankingStrategyParams;
import org.joda.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class MyServlet extends HttpServlet {


    private TradingStrategy tradingStrategy;

    @Inject
    MyServlet(TradingStrategy tradingStrategy) {
        this.tradingStrategy = tradingStrategy;

    }


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        tradingStrategy.execute(new LocalDate("2006-01-01"), new LocalDate("2011-06-01"),
                new ArrayList<Fund>(), new MomentumStrategyParams(1, new SimpleRankingStrategyParams(10, 9),3));
    }

}
