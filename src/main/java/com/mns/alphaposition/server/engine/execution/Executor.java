package com.mns.alphaposition.server.engine.execution;

import com.mns.alphaposition.server.engine.portfolio.Portfolio;

import java.math.BigDecimal;

public class Executor {

    private Portfolio portfolio;

    private BigDecimal transactionCost;

    public Executor(Portfolio portfolio, BigDecimal transactionCost) {
        this.portfolio = portfolio;
        this.transactionCost = transactionCost;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public BigDecimal getTransactionCost() {
        return transactionCost;
    }
}
