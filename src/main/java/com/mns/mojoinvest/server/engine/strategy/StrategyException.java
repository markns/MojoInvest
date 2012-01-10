package com.mns.mojoinvest.server.engine.strategy;

public class StrategyException extends Exception {

    public StrategyException(String messsage) {
        super(messsage);
    }

    public StrategyException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
