package com.mns.mojoinvest.server.handler;

import com.google.inject.Inject;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.shared.dispatch.GetParamDefaultsAction;
import com.mns.mojoinvest.shared.dispatch.GetParamDefaultsResult;

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
    public GetParamDefaultsResult execute(GetParamDefaultsAction action, ExecutionContext context) throws ActionException {

        BigDecimal investmentAmountDefault = new BigDecimal("10000");
        BigDecimal transactionCostDefault = new BigDecimal("12.95");
        //TODO: retrieve calculated ranges from database

        Integer performanceRangeDefault = 9;
        Integer rebalanceFrequencyDefault = 1;
        Integer portfolioSizeDefault = 3;
        Integer volatilityFilterDefault = 20;

        HashMap<String, Boolean> providers = new HashMap<String, Boolean>();
        for (String provider : fundDao.getProviders()) {
            providers.put(provider, true);
        }

        HashMap<String, Boolean> categories = new HashMap<String, Boolean>();
        for (String category : fundDao.getCategories()) {
            categories.put(category, true);
        }

        //TODO: retrieve earliest date from database
        Date fromDate = new Date(2000, 1, 1);
        Date toDate = new Date();

        String errorText = "";
        return new GetParamDefaultsResult(errorText, investmentAmountDefault, transactionCostDefault,
                performanceRangeDefault, rebalanceFrequencyDefault
                , portfolioSizeDefault, volatilityFilterDefault, providers, categories,
                fromDate, toDate);
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
