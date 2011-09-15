package com.mns.alphaposition.shared.params;

public class MomentumStrategyParams implements StrategyParams {

    private int rebalanceFrequency;

    private RankingStrategyParams rankingStrategyParams;

    private int portfolioSize;

    public MomentumStrategyParams() {
    }

    public MomentumStrategyParams(int rebalanceFrequency, RankingStrategyParams rankingStrategyParams,
                                  int portfolioSize) {
        this.rebalanceFrequency = rebalanceFrequency;
        this.rankingStrategyParams = rankingStrategyParams;
        this.portfolioSize = portfolioSize;
    }

    public int getRebalanceFrequency() {
        return rebalanceFrequency;
    }

    public RankingStrategyParams getRankingStrategyParams() {
        return rankingStrategyParams;
    }


    public int getPortfolioSize() {
        return portfolioSize;
    }
}
