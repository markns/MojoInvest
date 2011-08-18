package com.mns.alphaposition.server.engine.strategy;

import com.google.inject.Inject;
import com.mns.alphaposition.server.engine.execution.Executor;
import com.mns.alphaposition.server.engine.portfolio.Portfolio;
import com.mns.alphaposition.server.engine.portfolio.Position;
import com.mns.alphaposition.shared.engine.model.Fund;
import com.mns.alphaposition.shared.params.MomentumStrategyParams;
import com.mns.alphaposition.shared.util.TradingDayUtils;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class MomentumStrategy implements TradingStrategy<MomentumStrategyParams> {

    private final RankingStrategy rankingStrategy;
    private final Portfolio portfolio;
    private final Executor executor;

    @Inject
    public MomentumStrategy(RankingStrategy rankingStrategy, Executor executor, Portfolio portfolio) {
        this.rankingStrategy = rankingStrategy;
        this.portfolio = portfolio;
        this.executor = executor;
    }

    @Override
    public void execute(LocalDate fromDate, LocalDate toDate, List<Fund> funds,
                        MomentumStrategyParams params) {

        List<LocalDate> rebalanceDates = getRebalanceDates(fromDate, toDate, params);

        for (LocalDate rebalanceDate : rebalanceDates) {
            List<Fund> ranked = rankingStrategy.rank(rebalanceDate, funds, params.getRankingStrategyParams());
            List<Fund> selection;
            try {
                selection = getSelection(ranked, params);
            } catch (StrategyException e) {
                System.out.println(rebalanceDate + " " + e.getMessage());
                continue;
            }
            System.out.println("** " + rebalanceDate + " **");

            sellLosers(rebalanceDate, selection);
            buyWinners(params, rebalanceDate, selection);

            for (Map.Entry<Fund, Position> e : portfolio.getActivePositions().entrySet()) {
                System.out.println(e.getValue().getFund()
                        + " shares: " + e.getValue().shares()
                        + ", marketValue: " + e.getValue().marketValue(rebalanceDate)
                        + ", returnsGain: " + e.getValue().totalReturn(rebalanceDate)
                        + ", gain%: " + e.getValue().gainPercentage(rebalanceDate));

            }

            System.out.println("Overall return: " + portfolio.overallReturn(rebalanceDate));
        }
    }

    private void sellLosers(LocalDate rebalanceDate, List<Fund> selection) {
        for (Fund fund : portfolio.getActiveHoldings()) {
            if (!selection.contains(fund)) {
                executor.sellAll(fund, rebalanceDate);
            }
        }
    }

    private void buyWinners(MomentumStrategyParams params, LocalDate rebalanceDate, List<Fund> selection) {

        BigDecimal numEmpty = new BigDecimal(params.getPortfolioSize() - portfolio.numberOfActivePositions());
        BigDecimal availableCash = portfolio.getCash().
                subtract(executor.getTransactionCost().
                        multiply(numEmpty));
        System.out.println("Available cash: " + availableCash);
        for (Fund fund : selection) {
            if (!portfolio.contains(fund)) {
                BigDecimal allocation = availableCash
                        .divide(numEmpty, RoundingMode.HALF_DOWN);
                executor.buy(fund, rebalanceDate, allocation);
            }
        }
    }

    private List<Fund> getSelection(List<Fund> ranked, MomentumStrategyParams params)
            throws StrategyException {
        if (ranked.size() <= params.getPortfolioSize() * 2)
            throw new StrategyException("Not enough funds in population to make selection");
        return ranked.subList(0, params.getPortfolioSize());
    }

    private List<LocalDate> getRebalanceDates(LocalDate fromDate, LocalDate toDate, MomentumStrategyParams params) {
        //TODO: should handle rebalance frequency unit here - strategyParams.getRebalanceFrequency()
        return TradingDayUtils.getMonthlySeries(fromDate, toDate, true);
    }


}
