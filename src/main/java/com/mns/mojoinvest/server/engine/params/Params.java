package com.mns.mojoinvest.server.engine.params;


import java.util.List;

public class Params {

    private BacktestParams backtestParams;
    private StrategyParams strategyParams;
    private PortfolioParams portfolioParams;
    private List<String> universe;

    public Params(BacktestParams backtestParams, StrategyParams strategyParams,
                  PortfolioParams portfolioParams, List<String> universe) {
        this.backtestParams = backtestParams;
        this.strategyParams = strategyParams;
        this.portfolioParams = portfolioParams;
        this.universe = universe;
    }

    public BacktestParams getBacktestParams() {
        return backtestParams;
    }

    public void setBacktestParams(BacktestParams backtestParams) {
        this.backtestParams = backtestParams;
    }

    public StrategyParams getStrategyParams() {
        return strategyParams;
    }

    public void setStrategyParams(StrategyParams strategyParams) {
        this.strategyParams = strategyParams;
    }

    public PortfolioParams getPortfolioParams() {
        return portfolioParams;
    }

    public void setPortfolioParams(PortfolioParams portfolioParams) {
        this.portfolioParams = portfolioParams;
    }

    public List<String> getUniverse() {
        return universe;
    }

    public void setUniverse(List<String> universe) {
        this.universe = universe;
    }

    @Override
    public String toString() {
        return "Params{" +
                "backtestParams=" + backtestParams +
                '}';
    }
}
