package com.mns.mojoinvest.server.engine.model;

public class RankingParams {

    private int performanceRange;

    public RankingParams(int performanceRange) {
        this.performanceRange = performanceRange;
    }

    @Override
    public String toString() {
        return performanceRange + "";
    }
}