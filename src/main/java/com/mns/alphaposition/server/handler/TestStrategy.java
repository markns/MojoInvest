package com.mns.alphaposition.server.handler;

import com.google.inject.Inject;
import com.mns.alphaposition.server.PortfolioProvider;
import com.mns.alphaposition.server.engine.portfolio.Portfolio;


public class TestStrategy {

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
