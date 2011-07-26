package com.mns.alphaposition.server.engine.strategy;

import com.google.inject.Inject;
import com.mns.alphaposition.server.engine.execution.Executor;
import com.mns.alphaposition.shared.engine.model.Fund;
import com.mns.alphaposition.shared.params.MomentumStrategyParams;
import com.mns.alphaposition.shared.util.TradingDayUtils;
import org.joda.time.LocalDate;

import java.util.List;

public class MomentumStrategy implements TradingStrategy<MomentumStrategyParams> {

    private Executor executor;

    private RankingStrategy rankingStrategy;

    @Inject
    public MomentumStrategy(RankingStrategy rankingStrategy, Executor executor) {
        this.rankingStrategy = rankingStrategy;
        this.executor = executor;
    }

    @Override
    public void execute(LocalDate fromDate, LocalDate toDate, List<Fund> funds,
                        MomentumStrategyParams params) {

        List<LocalDate> rebalanceDates = getRebalanceDates(fromDate, toDate, params);

        for (LocalDate rebalanceDate : rebalanceDates) {
            List<Fund> ranked = rankingStrategy.rank(rebalanceDate, funds, params.getRankingStrategyParams());
            System.out.println(ranked);
        }

    }

    private List<LocalDate> getRebalanceDates(LocalDate fromDate, LocalDate toDate, MomentumStrategyParams params) {
        //TODO: should handle rebalance frequency unit here
        //strategyParams.getRebalanceFrequency()
        return TradingDayUtils.getMonthlySeries(fromDate, toDate);
    }


}
