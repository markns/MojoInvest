package com.mns.alphaposition.server;

import com.google.inject.Provider;
import com.google.inject.servlet.RequestScoped;
import com.mns.alphaposition.server.engine.params.PortfolioParams;
import com.mns.alphaposition.server.engine.portfolio.Portfolio;

import java.math.BigDecimal;

@RequestScoped
public class PortfolioProvider implements Provider<Portfolio> {

    Portfolio portfolio = null;

    public Portfolio get() {
        return portfolio;
    }

    public void setFlags(int flags) {
        portfolio = new Portfolio(new PortfolioParams(BigDecimal.ZERO, BigDecimal.ZERO), null);
    }
}