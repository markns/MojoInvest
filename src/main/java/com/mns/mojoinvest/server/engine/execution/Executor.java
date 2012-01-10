package com.mns.mojoinvest.server.engine.execution;

import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioException;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

public interface Executor {
    void buy(Portfolio portfolio, Fund fund, LocalDate date, BigDecimal allocation)
            throws PortfolioException;

    void sellAll(Portfolio portfolio, Fund fund, LocalDate date)
            throws PortfolioException;
}
