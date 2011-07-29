package com.mns.alphaposition.server.engine.execution;

import com.mns.alphaposition.server.engine.model.QuoteDao;
import com.mns.alphaposition.server.engine.portfolio.Portfolio;
import com.mns.alphaposition.server.engine.portfolio.Position;
import com.mns.alphaposition.server.engine.transaction.BuyTransaction;
import com.mns.alphaposition.server.engine.transaction.SellTransaction;
import com.mns.alphaposition.server.engine.transaction.Transaction;
import com.mns.alphaposition.shared.engine.model.Fund;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

public class NextTradingDayExecutor {

    private Portfolio portfolio;

    private BigDecimal transactionCost;

    private QuoteDao quoteDao;

    public NextTradingDayExecutor(Portfolio portfolio, BigDecimal transactionCost, QuoteDao quoteDao) {
        this.portfolio = portfolio;
        this.transactionCost = transactionCost;
        this.quoteDao = quoteDao;
    }

    public BigDecimal getTransactionCost() {
        return transactionCost;
    }

    public void buy(Fund fund, LocalDate date, BigDecimal allocation) {
        System.out.println("Buying " + fund + " " + allocation + " " + date);
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
