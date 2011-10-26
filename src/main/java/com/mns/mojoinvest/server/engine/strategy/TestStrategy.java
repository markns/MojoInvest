package com.mns.mojoinvest.server.engine.strategy;

import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioProvider;
import com.mns.mojoinvest.shared.params.StrategyParams;
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
