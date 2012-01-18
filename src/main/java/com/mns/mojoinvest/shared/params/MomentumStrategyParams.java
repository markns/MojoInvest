package com.mns.mojoinvest.shared.params;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MomentumStrategyParams implements IsSerializable {

    private int formationPeriod;

    private int holdingPeriod;

    private int portfolioSize;

    private Double correlation;

    //TODO: Remove this constructor
    public MomentumStrategyParams(int formationPeriod, int holdingPeriod, int portfolioSize) {
        this.formationPeriod = formationPeriod;
        this.holdingPeriod = holdingPeriod;
        this.portfolioSize = portfolioSize;
    }

    public MomentumStrategyParams(int formationPeriod, int holdingPeriod,
                                  int portfolioSize, double correlation) {
        this.formationPeriod = formationPeriod;
        this.holdingPeriod = holdingPeriod;
        this.portfolioSize = portfolioSize;
        this.correlation = correlation;
    }

    public MomentumStrategyParams() {
        //Serialization
    }

    public int getFormationPeriod() {
        return formationPeriod;
    }

    public void setFormationPeriod(int formationPeriod) {
        this.formationPeriod = formationPeriod;
    }

    public int getHoldingPeriod() {
        return holdingPeriod;
    }

    public void setHoldingPeriod(int holdingPeriod) {
        this.holdingPeriod = holdingPeriod;
    }

    public int getPortfolioSize() {
        return portfolioSize;
    }

    public void setPortfolioSize(int portfolioSize) {
        this.portfolioSize = portfolioSize;
    }

    public Double getCorrelation() {
        return correlation;
    }

    public void setCorrelation(Double correlation) {
        this.correlation = correlation;
    }

    @Override
    public String toString() {
        return "MomentumStrategyParams{" +
                "formationPeriod=" + formationPeriod +
                ", holdingPeriod=" + holdingPeriod +
                ", portfolioSize=" + portfolioSize +
                '}';
    }
}
