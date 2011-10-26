package com.mns.mojoinvest.shared.params;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.math.BigDecimal;

public class PortfolioParams implements IsSerializable {

    private BigDecimal initialInvestment;

    private BigDecimal transactionCost;

    public PortfolioParams() {
    }

    public PortfolioParams(BigDecimal initialInvestment, BigDecimal transactionCost) {
        this.initialInvestment = initialInvestment;
        this.transactionCost = transactionCost;
    }

    public BigDecimal getInitialInvestment() {
        return initialInvestment;
    }

    public BigDecimal getTransactionCost() {
        return transactionCost;
    }
}
