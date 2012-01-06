package com.mns.mojoinvest.server.engine.portfolio;

import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.servlet.http.HttpServletRequest;

public class PortfolioProvider implements Provider<Portfolio> {

    @Inject
    Provider<HttpServletRequest> requestProvider;


    public Portfolio get() {
        HttpServletRequest request = requestProvider.get();
        return (Portfolio) request.getAttribute("portfolio");
    }

}