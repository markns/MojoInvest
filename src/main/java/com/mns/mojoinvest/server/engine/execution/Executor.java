package com.mns.mojoinvest.server.engine.execution;

import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioException;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

public interface Executor {
    void buy(Portfolio portfolio, String fund, LocalDate date, BigDecimal allocation)
            throws PortfolioException;

    void sellAll(Portfolio portfolio, String fund, LocalDate date)
            throws PortfolioException;
}
