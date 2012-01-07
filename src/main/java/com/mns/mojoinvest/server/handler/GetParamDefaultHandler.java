package com.mns.mojoinvest.server.handler;

import com.google.inject.Inject;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.shared.dispatch.GetParamDefaultsAction;
import com.mns.mojoinvest.shared.dispatch.GetParamDefaultsResult;
import com.mns.mojoinvest.shared.params.BacktestParams;
import com.mns.mojoinvest.shared.params.MomentumStrategyParams;
import com.mns.mojoinvest.shared.params.PortfolioParams;

import java.math.BigDecimal;
import java.util.*;

public class GetParamDefaultHandler implements
        ActionHandler<GetParamDefaultsAction, GetParamDefaultsResult> {

    private final FundDao fundDao;

    @Inject
    public GetParamDefaultHandler(FundDao fundDao) {
        this.fundDao = fundDao;
    }

    @Override
    public GetParamDefaultsResult execute(GetParamDefaultsAction action, ExecutionContext context)
            throws ActionException {

        BigDecimal investmentAmountDefault = new BigDecimal("10000");
        BigDecimal transactionCostDefault = new BigDecimal("12.95");
        PortfolioParams portfolioParams = new PortfolioParams(investmentAmountDefault, transactionCostDefault);

        Integer formationPeriodDefault = 9;
        Integer holdingPeriodDefault = 1;
        Integer portfolioSizeDefault = 3;
        MomentumStrategyParams strategyParams = new MomentumStrategyParams(formationPeriodDefault,
                holdingPeriodDefault, portfolioSizeDefault);

        List<String> providers = fundDao.getProviders();
        List<String> categories = fundDao.getCategories();
        Date fromDate = new Date(2000, 1, 1); //TODO: retrieve earliest date from database
        Date toDate = new Date();
        BacktestParams backtestParams = new BacktestParams(fromDate, toDate, providers, categories);

        return new GetParamDefaultsResult("", portfolioParams, backtestParams, strategyParams);
    }

    @Override
    public Class<GetParamDefaultsAction> getActionType() {
        return GetParamDefaultsAction.class;
    }

    @Override
    public void undo(GetParamDefaultsAction action, GetParamDefaultsResult result, ExecutionContext context) throws ActionException {
        throw new UnsupportedOperationException();
    }
}
