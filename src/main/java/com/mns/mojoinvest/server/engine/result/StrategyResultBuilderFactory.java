package com.mns.mojoinvest.server.engine.result;

import com.mns.mojoinvest.server.engine.portfolio.Portfolio;

public interface StrategyResultBuilderFactory {

    public StrategyResultBuilder create(Portfolio portfolio);
}
