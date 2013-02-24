package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.server.engine.params.Params;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Portfolio {
    boolean isShadow();

    BigDecimal getCash(LocalDate date);

    BigDecimal getTransactionCost();

    boolean contains(String fund, LocalDate date);

    Position getPosition(String symbol);

    void add(Transaction transaction) throws PortfolioException;

    void add(BuyTransaction transaction) throws PortfolioException;

    void add(SellTransaction transaction) throws PortfolioException;

    List<Transaction> getTransactions();

    Collection<Position> getPositions();

    Map<String, Position> getOpenPositions(LocalDate date);

    int openPositionCount(LocalDate date);

    Collection<String> getActiveFunds(LocalDate date);

    BigDecimal costBasis(LocalDate date);

    BigDecimal cashOut(LocalDate date);

    BigDecimal marketValue(LocalDate date) throws PortfolioException;

    BigDecimal gain(LocalDate date) throws PortfolioException;

    BigDecimal gainPercentage(LocalDate date) throws PortfolioException;

    BigDecimal overallReturn(LocalDate date) throws PortfolioException;

    BigDecimal returnsGain(LocalDate date) throws PortfolioException;

    Params getParams();
}
