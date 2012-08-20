package com.mns.mojoinvest.server.engine.model.dao;

public class DataAccessException extends Throwable {
    public DataAccessException(Exception e) {
        super(e);
    }

    public DataAccessException(String message) {
        super(message);
    }
}
