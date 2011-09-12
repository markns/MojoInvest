package com.mns.alphaposition.server.engine.portfolio;

import com.google.inject.Provider;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
public class PortfolioProvider implements Provider<Portfolio> {

    private Portfolio portfolio = null;

    public Portfolio get() {
        if (portfolio == null)
            throw new NullPointerException("Portfolio hasn't been initialized");

        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
}