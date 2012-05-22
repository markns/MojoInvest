package com.mns.mojoinvest.server.params;

public class MomentumStrategyParams {

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

    @Override
    public String toString() {
        return "MomentumStrategyParams{" +
                "formationPeriod=" + formationPeriod +
                ", holdingPeriod=" + holdingPeriod +
                ", portfolioSize=" + portfolioSize +
                '}';
    }
}
