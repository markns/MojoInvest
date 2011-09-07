package com.mns.alphaposition.server.engine.params;

public class SimpleRankingStrategyParams implements RankingStrategyParams {

    private int averagingRange;

    private int performanceRange;

    public SimpleRankingStrategyParams(int averagingRange, int performanceRange) {
        this.averagingRange = averagingRange;
        this.performanceRange = performanceRange;
    }

    public int getAveragingRange() {
        return averagingRange;
    }

    public int getPerformanceRange() {
        return performanceRange;
    }
}
