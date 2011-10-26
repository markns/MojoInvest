package com.mns.mojoinvest.server.engine.strategy;

import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.shared.params.StrategyParams;
import org.joda.time.LocalDate;

import java.util.List;

public interface TradingStrategy {

    void execute(LocalDate fromDate, LocalDate toDate, List<Fund> funds, StrategyParams strategyParams)
            throws StrategyException;

    boolean supports(StrategyParams strategyParams);
}

