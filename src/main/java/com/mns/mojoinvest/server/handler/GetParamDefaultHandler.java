package com.mns.mojoinvest.server.handler;

import com.google.inject.Inject;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.shared.dispatch.GetParamDefaultsAction;
import com.mns.mojoinvest.shared.dispatch.GetParamDefaultsResult;
import com.mns.mojoinvest.shared.params.*;
import org.joda.time.LocalDate;

import java.util.Date;
import java.util.List;

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

        //TODO: When user login is enabled, this method should retrieve user defaults if available

        Double investmentAmountDefault = Double.valueOf("10000");
        Double transactionCostDefault = Double.valueOf("12.95");
        PortfolioParams portfolioParams = new PortfolioParams(investmentAmountDefault, transactionCostDefault);

        Integer formationPeriodDefault = 9;
        Integer holdingPeriodDefault = 1;
        Integer portfolioSizeDefault = 3;
        MomentumStrategyParams strategyParams = new MomentumStrategyParams(formationPeriodDefault,
                holdingPeriodDefault, portfolioSizeDefault);

        //TODO: retrieve earliest date from database
        Date fromDate = new LocalDate("2007-01-01").toDateMidnight().toDate();
        Date toDate = new LocalDate("2010-09-01").toDateMidnight().toDate();
        BacktestParams backtestParams = new BacktestParams(fromDate, toDate);

        List<String> providers = fundDao.getProviders();
        List<String> categories = fundDao.getCategories();
        FundFilter fundFilter = new FundFilter(providers, categories);

        Params params = new Params(backtestParams, strategyParams, portfolioParams, fundFilter);

        return new GetParamDefaultsResult("", params);
    }

    @Override
    public Class<GetParamDefaultsAction> getActionType() {
        return GetParamDefaultsAction.class;
    }

    @Override
    public void undo(GetParamDefaultsAction action, GetParamDefaultsResult result, ExecutionContext context)
            throws ActionException {
        throw new UnsupportedOperationException();
    }
}
