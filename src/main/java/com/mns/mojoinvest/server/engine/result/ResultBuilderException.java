package com.mns.mojoinvest.server.engine.result;

import com.mns.mojoinvest.server.engine.portfolio.PortfolioException;

public class ResultBuilderException extends Throwable {
    public ResultBuilderException(String s) {
        super(s);
    }

    public ResultBuilderException(String message, PortfolioException e) {
        super(message, e);
    }
}
