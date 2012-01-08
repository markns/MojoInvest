package com.mns.mojoinvest.shared.params;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PortfolioParams implements IsSerializable {

    private Double initialInvestment;

    private Double transactionCost;

    public PortfolioParams(Double initialInvestment, Double transactionCost) {
        this.initialInvestment = initialInvestment;
        this.transactionCost = transactionCost;
    }

    public PortfolioParams() {
        //For serialization
    }

    public Double getInitialInvestment() {
        return initialInvestment;
    }

    public void setInitialInvestment(Double initialInvestment) {
        this.initialInvestment = initialInvestment;
    }

    public Double getTransactionCost() {
        return transactionCost;
    }

    public void setTransactionCost(Double transactionCost) {
        this.transactionCost = transactionCost;
    }

    @Override
    public String toString() {
        return "PortfolioParams{" +
                "initialInvestment=" + initialInvestment +
                ", transactionCost=" + transactionCost +
                '}';
    }
}
