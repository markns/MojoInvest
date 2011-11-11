package com.mns.mojoinvest.server.engine.model;

import java.io.Serializable;

public class RankingParams implements Serializable {

    private int performanceRange;

    public RankingParams(int performanceRange) {
        this.performanceRange = performanceRange;
    }

    public int getPerformanceRange() {
        return performanceRange;
    }

    @Override
    public String toString() {
        return performanceRange + "";
    }
}
