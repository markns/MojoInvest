package com.mns.alphaposition.server.handler;

import com.google.inject.Inject;
import com.mns.alphaposition.server.engine.model.Fund;
import com.mns.alphaposition.server.engine.portfolio.Portfolio;
import com.mns.alphaposition.server.engine.portfolio.PortfolioProvider;
import com.mns.alphaposition.server.engine.strategy.TradingStrategy;
import com.mns.alphaposition.shared.params.StrategyParams;
import org.joda.time.LocalDate;

import java.util.List;


public class TestStrategy implements TradingStrategy<TestStrategy.TestParams> {

    public static class TestParams implements StrategyParams {

    }

    @Override
    public void execute(LocalDate fromDate, LocalDate toDate, List<Fund> funds, TestParams strategyParams) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private PortfolioProvider portfolioProvider;

    @Inject
    public TestStrategy(PortfolioProvider portfolioProvider) {
        this.portfolioProvider = portfolioProvider;
    }

    public void execute() {
        Portfolio portfolio = portfolioProvider.get();
        System.out.println(portfolio.getCash());
    }

}
