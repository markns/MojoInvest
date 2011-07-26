package com.mns.alphaposition.shared.params;

import org.joda.time.LocalDate;

import java.util.Map;

public class BackTestParams {

    private LocalDate fromDate;

    private LocalDate toDate;

    private Map<String, String> fundsFilter;

    private StrategyParams strategyParams;

    public BackTestParams(LocalDate fromDate, LocalDate toDate,
                          Map<String, String> fundsFilter, StrategyParams strategyParams) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fundsFilter = fundsFilter;
        this.strategyParams = strategyParams;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public Map<String, String> getFundsFilter() {
        return fundsFilter;
    }

    public StrategyParams getStrategyParams() {
        return strategyParams;
    }
}
