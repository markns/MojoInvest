package com.mns.alphaposition.server.engine.strategy;

import com.google.inject.Inject;
import com.mns.alphaposition.server.engine.execution.Executor;
import com.mns.alphaposition.server.engine.model.QuoteDao;
import com.mns.alphaposition.shared.engine.model.Fund;
import com.mns.alphaposition.shared.engine.model.Quote;
import com.mns.alphaposition.shared.params.MomentumStrategyParams;
import com.mns.alphaposition.shared.util.TradingDayUtils;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;

public class MomentumStrategy implements TradingStrategy<MomentumStrategyParams> {

    private QuoteDao quoteDao;

    private Executor executor;

    @Inject
    public MomentumStrategy(QuoteDao quoteDao, Executor executor) {
        this.quoteDao = quoteDao;
        this.executor = executor;
    }

    @Override
    public void execute(LocalDate fromDate, LocalDate toDate, List<Fund> funds,
                        MomentumStrategyParams strategyParams) {

        List<LocalDate> rebalanceDates = getRebalanceDates(fromDate, toDate, strategyParams);
        Collection<Quote> quotes = quoteDao.get(funds, rebalanceDates);
        System.out.println(quotes);
    }

    private List<LocalDate> getRebalanceDates(LocalDate fromDate, LocalDate toDate, MomentumStrategyParams strategyParams) {
        //TODO: should check rebalance frequency here
        //strategyParams.getRebalanceFrequency()
        return TradingDayUtils.getMonthlySeries(fromDate, toDate);
    }
}
