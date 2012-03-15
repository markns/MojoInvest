package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Portfolio {
    BigDecimal getCash(LocalDate date);

    BigDecimal getTransactionCost();

    boolean contains(String fund, LocalDate date);

    Position getPosition(String fund);

    void add(Transaction transaction) throws PortfolioException;

    void add(BuyTransaction transaction) throws PortfolioException;

    void add(SellTransaction transaction) throws PortfolioException;

    Collection<Position> getPositions();

    Map<String, Position> getOpenPositions(LocalDate date);

    int openPositionCount(LocalDate date);

    Collection<String> getFunds();

    Collection<String> getActiveFunds(LocalDate date);

    BigDecimal costBasis(LocalDate date);

    BigDecimal cashOut(LocalDate date);

    BigDecimal marketValue(LocalDate date);

    BigDecimal gain(LocalDate date);

    BigDecimal gainPercentage(LocalDate date);

    BigDecimal overallReturn(LocalDate date);

    BigDecimal returnsGain(LocalDate date);

    List<BigDecimal> marketValue(List<LocalDate> dates);
}
