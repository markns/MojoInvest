package com.mns.alphaposition.server.engine.execution;

import com.mns.alphaposition.server.engine.portfolio.Portfolio;
import com.mns.alphaposition.server.engine.portfolio.Position;
import com.mns.alphaposition.server.engine.transaction.BuyTransaction;
import com.mns.alphaposition.server.engine.transaction.SellTransaction;
import com.mns.alphaposition.server.engine.transaction.Transaction;
import com.mns.alphaposition.shared.engine.model.Fund;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

public class NextTradingDayExecutor implements Executor {

    private Portfolio portfolio;

    private BigDecimal transactionCost;

    public NextTradingDayExecutor(Portfolio portfolio, BigDecimal transactionCost) {
        this.portfolio = portfolio;
        this.transactionCost = transactionCost;
    }

    public BigDecimal getTransactionCost() {
        return transactionCost;
    }

    public void buy(Fund fund, LocalDate date, BigDecimal allocation) {
        System.out.println("buying " + fund + " " + allocation + " " + date);
        //TODO: getExecutionPrice()
        BigDecimal executionPrice = BigDecimal.ONE;
        Transaction tx = new BuyTransaction(fund, date, allocation, executionPrice, transactionCost);
        portfolio.add(tx);
    }

    public void sellAll(Fund fund, LocalDate date) {
        System.out.println("Selling " + fund + " " + date);
        //TODO: getExecutionPrice()
        BigDecimal executionPrice = BigDecimal.ONE;
        Position position = portfolio.get(fund);
        Transaction tx = new SellTransaction(fund, date, position.shares(), executionPrice, transactionCost);
        portfolio.add(tx);
    }
}
