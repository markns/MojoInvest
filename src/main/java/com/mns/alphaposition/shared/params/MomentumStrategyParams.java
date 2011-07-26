package com.mns.alphaposition.shared.params;

public class MomentumStrategyParams<T> implements StrategyParams<T> {

    private int rebalanceFrequency;

    private RankingStrategyParams rankingStrategyParams;

    public MomentumStrategyParams(int rebalanceFrequency, RankingStrategyParams rankingStrategyParams) {
        this.rebalanceFrequency = rebalanceFrequency;
        this.rankingStrategyParams = rankingStrategyParams;
    }

    public int getRebalanceFrequency() {
        return rebalanceFrequency;
    }

    public RankingStrategyParams getRankingStrategyParams() {
        return rankingStrategyParams;
    }


}
