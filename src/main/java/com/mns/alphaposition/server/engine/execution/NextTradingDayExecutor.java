package com.mns.alphaposition.server.engine.execution;

import com.mns.alphaposition.server.engine.model.QuoteDao;
import com.mns.alphaposition.server.engine.portfolio.Portfolio;
import com.mns.alphaposition.server.engine.portfolio.Position;
import com.mns.alphaposition.server.engine.transaction.BuyTransaction;
import com.mns.alphaposition.server.engine.transaction.SellTransaction;
import com.mns.alphaposition.server.engine.transaction.Transaction;
import com.mns.alphaposition.shared.engine.model.Fund;
import com.mns.alphaposition.shared.engine.model.Quote;
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
        //TODO: get execution price should be mid between open and close
        Quote executionQuote = quoteDao.get(fund, date);
        System.out.println("Buying " + fund + " " + allocation + " " + date + " " + executionQuote.getClose());
        BigDecimal shares = allocation.divide(executionQuote.getClose(), 0, BigDecimal.ROUND_DOWN);
        Transaction tx = new BuyTransaction(fund, date, shares, executionQuote.getClose(), transactionCost);
        portfolio.add(tx);
    }

    public void sellAll(Fund fund, LocalDate date) {
        //TODO: get execution price should be mid between open and close
        Quote executionQuote = quoteDao.get(fund, date);
        System.out.println("Selling " + fund + " " + date + " " + executionQuote.getClose());
        Position position = portfolio.get(fund);
        Transaction tx = new SellTransaction(fund, date, position.shares(), executionQuote.getClose(), transactionCost);
        portfolio.add(tx);
    }
}
