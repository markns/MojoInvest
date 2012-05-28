package com.mns.mojoinvest.server.params;


public class Params {

    private BacktestParams backtestParams;

    public Params(BacktestParams backtestParams) {
        this.backtestParams = backtestParams;
    }

    public Params() {
        //For serialization
    }

    public BacktestParams getBacktestParams() {
        return backtestParams;
    }

    @Override
    public String toString() {
        return "Params{" +
                "backtestParams=" + backtestParams +
                '}';
    }
}
