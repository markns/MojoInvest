package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public interface Portfolio {
    BigDecimal getCash();

    BigDecimal getTransactionCost();

    boolean contains(Fund fund, LocalDate date);

    Position getPosition(Fund fund);

    void add(BuyTransaction transaction) throws PortfolioException;

    void add(SellTransaction transaction) throws PortfolioException;

    Collection<Position> getPositions();

    Map<Fund, Position> getOpenPositions(LocalDate date);

    int openPositionCount(LocalDate date);

    Collection<Fund> getActiveFunds(LocalDate date);

    BigDecimal costBasis(LocalDate date);

    BigDecimal cashOut(LocalDate date);

    BigDecimal marketValue(LocalDate date);

    BigDecimal gain(LocalDate date);

    BigDecimal gainPercentage(LocalDate date);

    BigDecimal overallReturn(LocalDate date);

    BigDecimal returnsGain(LocalDate date);
}
