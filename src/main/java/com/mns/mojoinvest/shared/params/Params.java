package com.mns.mojoinvest.shared.params;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Params implements IsSerializable {

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
}
