package com.mns.mojoinvest.server.engine.portfolio;

public class PortfolioException extends Exception {
    public PortfolioException(String message) {
        super(message);
    }

    public PortfolioException(String message, Throwable e) {
        super(message, e);
    }
}
