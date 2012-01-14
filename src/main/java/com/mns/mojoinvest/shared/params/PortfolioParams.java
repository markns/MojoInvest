package com.mns.mojoinvest.shared.params;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;

public class PortfolioParams implements IsSerializable {

    private Date creationDate;
    private Double initialInvestment;
    private Double transactionCost;

    public PortfolioParams(Double initialInvestment, Double transactionCost) {
        this.initialInvestment = initialInvestment;
        this.transactionCost = transactionCost;
    }

    public PortfolioParams(Double initialInvestment, Double transactionCost, Date creationDate) {
        this.initialInvestment = initialInvestment;
        this.transactionCost = transactionCost;
        this.creationDate = creationDate;
    }

    public PortfolioParams() {
        //For serialization
    }

    public Double getInitialInvestment() {
        return initialInvestment;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return creationDate;
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
