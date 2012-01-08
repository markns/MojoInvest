package com.mns.mojoinvest.server.handler;

import com.google.inject.Inject;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.portfolio.Lot;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioFactory;
import com.mns.mojoinvest.server.engine.portfolio.Position;
import com.mns.mojoinvest.server.engine.strategy.MomentumStrategy;
import com.mns.mojoinvest.server.engine.strategy.StrategyException;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import com.mns.mojoinvest.shared.dispatch.RunStrategyAction;
import com.mns.mojoinvest.shared.dispatch.RunStrategyResult;
import com.mns.mojoinvest.shared.dto.StrategyResult;
import com.mns.mojoinvest.shared.dto.TransactionDto;
import com.mns.mojoinvest.shared.params.FundFilter;
import com.mns.mojoinvest.shared.params.Params;

import java.util.*;

public class RunStrategyHandler implements
        ActionHandler<RunStrategyAction, RunStrategyResult> {

    private MomentumStrategy strategy;

    private final PortfolioFactory portfolioFactory;

    private final FundDao fundDao;

    @Inject
    public RunStrategyHandler(MomentumStrategy strategy,
                              PortfolioFactory portfolioFactory,
                              FundDao fundDao) {
        this.strategy = strategy;
        this.portfolioFactory = portfolioFactory;
        this.fundDao = fundDao;
    }

    @Override
    public RunStrategyResult execute(final RunStrategyAction action,
                                     final ExecutionContext context) throws ActionException {

        Params params = action.getParams();

        //TODO: Does the portfolioFactory need to be synchronised?
        Portfolio portfolio = portfolioFactory.create(params.getPortfolioParams());

        //TODO: Abstract the getAcceptableFunds call to a separate class
        Set<Fund> funds = getAcceptableFunds(params.getFundFilter());

        StrategyResult result;
        try {
            strategy.execute(portfolio, params.getBacktestParams(),
                    funds, params.getStrategyParams());

            //TODO: Abstract the createPortfolioResults to a separate class
            result = createStrategyResults(portfolio);
        } catch (StrategyException e) {
            e.printStackTrace();
            throw new ActionException(e);
        }

        return new RunStrategyResult("", result);
    }

    private StrategyResult createStrategyResults(Portfolio portfolio) {

        List<Transaction> transactions = new ArrayList<Transaction>();
        for (Position position : portfolio.getPositions()) {
            for (Lot lot : position.getLots()) {
                transactions.add(lot.getOpeningTransaction());
                transactions.addAll(lot.getClosingTransactions());
            }
        }
        sortByDate(transactions);

        List<TransactionDto> transactionDtos = new ArrayList<TransactionDto>();
        for (Transaction transaction : transactions) {
            if (transaction instanceof BuyTransaction) {
                transactionDtos.add(new TransactionDto("Buy", transaction.getFund().getSymbol(), transaction.getFund().getName(),
                        transaction.getDate().toDateMidnight().toDate(), transaction.getQuantity().doubleValue(),
                        transaction.getPrice().doubleValue(), transaction.getCommission().doubleValue()));
            } else if (transaction instanceof SellTransaction) {
                transactionDtos.add(new TransactionDto("Sell", transaction.getFund().getSymbol(), transaction.getFund().getName(),
                        transaction.getDate().toDateMidnight().toDate(), transaction.getQuantity().doubleValue(),
                        transaction.getPrice().doubleValue(), transaction.getCommission().doubleValue()));
            }
        }

        return new StrategyResult(transactionDtos);
    }

    public static void sortByDate(List<Transaction> transactions) {
        Collections.sort(transactions, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction q1, Transaction q2) {
                return q1.getDate().compareTo(q2.getDate());
            }
        });
    }

    private Set<Fund> getAcceptableFunds(FundFilter fundFilter) {
        Map<String, Object> filter = new HashMap<String, Object>(2);
        if (fundFilter.getProviders().size() > 0)
            filter.put("provider in", fundFilter.getProviders());
        if (fundFilter.getCategories().size() > 0)
            filter.put("category in", fundFilter.getCategories());
        return new HashSet<Fund>(fundDao.query(filter));
    }

    @Override
    public Class<RunStrategyAction> getActionType() {
        return RunStrategyAction.class;
    }

    @Override
    public void undo(final RunStrategyAction action,
                     final RunStrategyResult result, final ExecutionContext context)
            throws ActionException {
        // No undo
    }
}
