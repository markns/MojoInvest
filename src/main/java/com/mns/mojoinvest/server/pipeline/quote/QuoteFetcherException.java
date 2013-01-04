package com.mns.mojoinvest.server.pipeline.quote;

public class QuoteFetcherException extends Throwable {
    public QuoteFetcherException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public QuoteFetcherException(String s) {
        super(s);
    }
}
