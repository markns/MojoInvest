package com.mns.mojoinvest.server.pipeline;

public class PipelineException extends RuntimeException {

    public PipelineException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public PipelineException(String message) {
        super(message);
    }
}
