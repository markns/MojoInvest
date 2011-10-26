package com.mns.mojoinvest.server.engine.strategy;

import com.google.inject.Inject;
import com.googlecode.objectify.NotFoundException;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.FundDao;
import com.mns.mojoinvest.server.engine.model.Ranking;
import com.mns.mojoinvest.server.engine.model.RankingDao;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioProvider;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import com.mns.mojoinvest.shared.params.MomentumStrategyParams;
import com.mns.mojoinvest.shared.params.StrategyParams;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

public class MomentumStrategy implements TradingStrategy {

    private static final Logger log = Logger.getLogger(MomentumStrategy.class.getName());

    private final RankingStrategy rankingStrategy;
    private final PortfolioProvider portfolioProvider;
    private final Executor executor;

    private final RankingDao rankingDao;
    private final FundDao fundDao;

    @Inject
    public MomentumStrategy(RankingStrategy rankingStrategy, Executor executor,
                            PortfolioProvider portfolioProvider, RankingDao rankingDao,
                            FundDao fundDao) {
        this.rankingStrategy = rankingStrategy;
        this.portfolioProvider = portfolioProvider;
        this.executor = executor;
        this.rankingDao = rankingDao;
        this.fundDao = fundDao;
    }

    @Override
    public void execute(LocalDate fromDate, LocalDate toDate, List<Fund> acceptableFunds,
                        StrategyParams strategyParams) throws StrategyException {

        if (!supports(strategyParams))
            throw new StrategyException(this.getClass() + " doesn't support " + strategyParams.getClass());

        MomentumStrategyParams params = (MomentumStrategyParams) strategyParams;

        List<LocalDate> rebalanceDates = getRebalanceDates(fromDate, toDate, params);

        for (LocalDate rebalanceDate : rebalanceDates) {
            try {
                Ranking ranking = rankingDao.get(rebalanceDate);
                Collection<Fund> selection = getSelection(ranking.getM9(), acceptableFunds, params);

                sellLosers(rebalanceDate, selection);
                buyWinners(params, rebalanceDate, selection);
            } catch (NotFoundException e) {
                log.info(rebalanceDate + " " + e.getMessage());
            } catch (StrategyException e) {
                log.info(rebalanceDate + " " + e.getMessage());
            }

//            for (Position position : portfolio().getActivePositions().values()) {
//                log.info(position.getFund()
//                        + " shares: " + position.shares()
//                        + ", marketValue: " + position.marketValue(rebalanceDate)
//                        + ", returnsGain: " + position.totalReturn(rebalanceDate)
//                        + ", gain%: " + position.gainPercentage(rebalanceDate));
//
//            }
            log.info(rebalanceDate + " portfolio value: " + portfolio().marketValue(rebalanceDate) + ", holdings: " +
                    portfolio().getActiveHoldings());
        }
        log.info(toDate + " portfolio value: " + portfolio().marketValue(toDate));
    }

    @Override
    public boolean supports(StrategyParams strategyParams) {
        return strategyParams instanceof MomentumStrategyParams;
    }

    private Portfolio portfolio() {
        return portfolioProvider.get();
    }

    private Collection<Fund> getSelection(List<String> ranked, List<Fund> acceptableFunds,
                                          MomentumStrategyParams params) throws StrategyException {
        if (ranked.size() <= params.getPortfolioSize() * 2)
            throw new StrategyException("Not enough funds in population to make selection");
        Collection<Fund> funds = fundDao.get(ranked);
        Collection<Fund> selection = new ArrayList<Fund>(params.getPortfolioSize());
        for (Fund fund : funds) {
            if (acceptableFunds.contains(fund))
                selection.add(fund);
            if (selection.size() == params.getPortfolioSize()) {
                break;
            }
        }
        return selection;
    }

    private void sellLosers(LocalDate rebalanceDate, Collection<Fund> selection) {
        for (Fund fund : portfolio().getActiveHoldings()) {
            if (!selection.contains(fund)) {
                executor.sellAll(fund, rebalanceDate);
            }
        }
    }

    private void buyWinners(MomentumStrategyParams params, LocalDate rebalanceDate, Collection<Fund> selection) {

        BigDecimal numEmpty = new BigDecimal(params.getPortfolioSize() - portfolio().numberOfActivePositions());
        BigDecimal availableCash = portfolio().getCash().
                subtract(executor.getTransactionCost().
                        multiply(numEmpty));
//        log.info("Available cash: " + availableCash);
        for (Fund fund : selection) {
            if (!portfolio().contains(fund)) {
                BigDecimal allocation = availableCash
                        .divide(numEmpty, RoundingMode.HALF_DOWN);
                executor.buy(fund, rebalanceDate, allocation);
            }
        }
    }

    private List<LocalDate> getRebalanceDates(LocalDate fromDate, LocalDate toDate, MomentumStrategyParams params) {
        return TradingDayUtils.getMonthlySeries(fromDate, toDate, params.getRebalanceFrequency(), true);
    }


}
