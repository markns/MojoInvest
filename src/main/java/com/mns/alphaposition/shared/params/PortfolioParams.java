package com.mns.alphaposition.shared.params;

import java.math.BigDecimal;

public class PortfolioParams {

    private BigDecimal initialInvestment;

    private BigDecimal transactionCost;

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
