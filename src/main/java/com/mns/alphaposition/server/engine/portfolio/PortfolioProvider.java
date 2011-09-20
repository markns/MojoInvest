package com.mns.alphaposition.server.engine.portfolio;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.servlet.RequestScoped;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public class PortfolioProvider implements Provider<Portfolio> {

//    private Portfolio portfolio = null;

    @Inject
    Provider<HttpServletRequest> requestProvider;


    public Portfolio get() {
        HttpServletRequest request = requestProvider.get();
        return (Portfolio) request.getAttribute("portfolio");

//        if (portfolio == null)
//            throw new NullPointerException("Portfolio hasn't been initialized");
//        return portfolio;
    }

//    public void setPortfolio(Portfolio portfolio) {
//        if (this.portfolio != null)
//            throw new IllegalStateException("Not possible to change portfolio once it has been set.");
//        this.portfolio = portfolio;
//    }
}