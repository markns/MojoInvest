package com.mns.alphaposition.server.engine.strategy;

import com.mns.alphaposition.server.engine.model.Fund;
import com.mns.alphaposition.shared.params.StrategyParams;
import org.joda.time.LocalDate;

import java.util.List;

public interface TradingStrategy {

    void execute(LocalDate fromDate, LocalDate toDate, List<Fund> funds, StrategyParams strategyParams)
            throws StrategyException;

    boolean supports(StrategyParams strategyParams);
}

