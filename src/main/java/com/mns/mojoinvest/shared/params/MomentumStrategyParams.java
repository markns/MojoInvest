package com.mns.mojoinvest.shared.params;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MomentumStrategyParams implements IsSerializable {

    private int rebalanceFrequency;

    private int averagingRange;

    private int performanceRange;

    private int portfolioSize;

    public MomentumStrategyParams(int rebalanceFrequency, int averagingRange, int performanceRange, int portfolioSize) {
        this.rebalanceFrequency = rebalanceFrequency;
        this.averagingRange = averagingRange;
        this.performanceRange = performanceRange;
        this.portfolioSize = portfolioSize;
    }

    public MomentumStrategyParams() {
        //Serialization
    }

    public int getRebalanceFrequency() {
        return rebalanceFrequency;
    }

    public int getAveragingRange() {
        return averagingRange;
    }

    public int getPerformanceRange() {
        return performanceRange;
    }

    public int getPortfolioSize() {
        return portfolioSize;
    }
}
