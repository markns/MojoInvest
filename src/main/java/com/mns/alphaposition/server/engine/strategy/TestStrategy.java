package com.mns.alphaposition.server.engine.strategy;

import com.google.inject.Inject;
import com.mns.alphaposition.server.engine.model.Fund;
import com.mns.alphaposition.server.engine.portfolio.PortfolioProvider;
import com.mns.alphaposition.shared.params.StrategyParams;
import org.joda.time.LocalDate;

import java.util.List;


public class TestStrategy implements TradingStrategy {

    private PortfolioProvider portfolioProvider;

    @Inject
    public TestStrategy(PortfolioProvider portfolioProvider) {
        this.portfolioProvider = portfolioProvider;
    }

    @Override
    public void execute(LocalDate fromDate, LocalDate toDate, List<Fund> funds, StrategyParams strategyParams) {

    }

    @Override
    public boolean supports(StrategyParams strategyParams) {
        return false;
    }

}
