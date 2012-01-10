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

    boolean contains(Fund fund);

    Position get(Fund fund);

    void add(BuyTransaction transaction) throws PortfolioException;

    void add(SellTransaction transaction) throws PortfolioException;

    int numberOfActivePositions();

    Collection<Position> getPositions();

    HashMap<Fund, Position> getActivePositions();

    Set<Fund> getActiveHoldings();

    BigDecimal costBasis();

    BigDecimal marketValue(LocalDate date);

    BigDecimal gain(LocalDate date);

    BigDecimal todaysGain(BigDecimal priceChange);

    BigDecimal gainPercentage(LocalDate date);

    BigDecimal overallReturn(LocalDate date);

    BigDecimal returnsGain(LocalDate date);

    BigDecimal cashOut();
}
