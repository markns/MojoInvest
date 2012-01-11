package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public interface Portfolio {
    BigDecimal getCash();

    BigDecimal getTransactionCost();

    boolean contains(Fund fund, LocalDate date);

    Position get(Fund fund);

    void add(BuyTransaction transaction) throws PortfolioException;

    void add(SellTransaction transaction) throws PortfolioException;

    int numberOfActivePositions(LocalDate date);

    Collection<Position> getPositions();

    HashMap<Fund, Position> getActivePositions(LocalDate date);

    Set<Fund> getActiveHoldings(LocalDate date);

    BigDecimal costBasis(LocalDate date);

    BigDecimal marketValue(LocalDate date);

    BigDecimal gain(LocalDate date);

    BigDecimal todaysGain(LocalDate date, BigDecimal priceChange);

    BigDecimal gainPercentage(LocalDate date);

    BigDecimal overallReturn(LocalDate date);

    BigDecimal returnsGain(LocalDate date);

    BigDecimal cashOut(LocalDate date);
}
