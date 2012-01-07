package com.mns.mojoinvest.shared.params;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MomentumStrategyParams implements IsSerializable {

    private int formationPeriod;

    private int holdingPeriod;

    private int portfolioSize;

    public MomentumStrategyParams(int formationPeriod, int holdingPeriod, int portfolioSize) {
        this.formationPeriod = formationPeriod;
        this.holdingPeriod = holdingPeriod;
        this.portfolioSize = portfolioSize;
    }

    public MomentumStrategyParams() {
        //Serialization
    }

    public int getFormationPeriod() {
        return formationPeriod;
    }

    public int getHoldingPeriod() {
        return holdingPeriod;
    }

    public int getPortfolioSize() {
        return portfolioSize;
    }
}
