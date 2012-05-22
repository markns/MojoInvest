package com.mns.mojoinvest.server.params;


public class Params {

    private BacktestParams backtestParams;
    private MomentumStrategyParams strategyParams;
    private PortfolioParams portfolioParams;
    private FundFilter fundFilter;

    public Params(BacktestParams backtestParams, MomentumStrategyParams strategyParams, PortfolioParams portfolioParams, FundFilter fundFilter) {
        this.backtestParams = backtestParams;
        this.strategyParams = strategyParams;
        this.portfolioParams = portfolioParams;
        this.fundFilter = fundFilter;
    }

    public Params() {
        //For serialization
    }

    public BacktestParams getBacktestParams() {
        return backtestParams;
    }

    public MomentumStrategyParams getStrategyParams() {
        return strategyParams;
    }

    public PortfolioParams getPortfolioParams() {
        return portfolioParams;
    }

    public FundFilter getFundFilter() {
        return fundFilter;
    }

    @Override
    public String toString() {
        return "Params{" +
                "backtestParams=" + backtestParams +
                ", strategyParams=" + strategyParams +
                ", portfolioParams=" + portfolioParams +
                ", fundFilter=" + fundFilter +
                '}';
    }
}
