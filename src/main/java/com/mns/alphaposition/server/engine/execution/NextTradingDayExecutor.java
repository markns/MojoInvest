package com.mns.alphaposition.server.engine.execution;

import com.google.inject.Inject;
import com.mns.alphaposition.server.engine.model.QuoteDao;
import com.mns.alphaposition.server.engine.portfolio.Portfolio;
import com.mns.alphaposition.server.engine.portfolio.Position;
import com.mns.alphaposition.server.engine.transaction.BuyTransaction;
import com.mns.alphaposition.server.engine.transaction.SellTransaction;
import com.mns.alphaposition.shared.engine.model.Fund;
import com.mns.alphaposition.shared.engine.model.Quote;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

public class NextTradingDayExecutor implements Executor {

    private Portfolio portfolio;

    private BigDecimal transactionCost;

    private QuoteDao quoteDao;

    @Inject
    public NextTradingDayExecutor(Portfolio portfolio, BigDecimal transactionCost, QuoteDao quoteDao) {
        this.portfolio = portfolio;
        this.transactionCost = transactionCost;
        this.quoteDao = quoteDao;
    }

    @Override
    public BigDecimal getTransactionCost() {
        return transactionCost;
    }

    @Override
    public void buy(Fund fund, LocalDate date, BigDecimal allocation) {
        //TODO: get execution price should be mid between open and close
        Quote executionQuote = quoteDao.get(fund, date);
        BigDecimal shares = allocation.divide(executionQuote.getClose(), 0, BigDecimal.ROUND_DOWN);
        System.out.println("Buying " + fund + " amount: " + allocation + ", price: " + executionQuote.getClose() + ", shares: " + shares);
        BuyTransaction tx = new BuyTransaction(fund, date, shares, executionQuote.getClose(), transactionCost);
        portfolio.add(tx);
    }

    @Override
    public void sellAll(Fund fund, LocalDate date) {
        //TODO: get execution price should be mid between open and close
        Quote executionQuote = quoteDao.get(fund, date);
        Position position = portfolio.get(fund);
        System.out.println("Selling " + fund + " price: " + executionQuote.getClose() + ", gain%: " + position.gainPercentage(date));
        SellTransaction tx = new SellTransaction(fund, date, position.shares(), executionQuote.getClose(), transactionCost);
        portfolio.add(tx);
    }
}
