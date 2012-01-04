package com.mns.mojoinvest.server.handler;

import com.google.inject.Inject;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.shared.dispatch.GetParamsStaticAndDefaultsAction;
import com.mns.mojoinvest.shared.dispatch.GetParamsStaticAndDefaultsResult;
import org.apache.commons.lang.NotImplementedException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GetParamsStaticAndDefaultHandler implements
        ActionHandler<GetParamsStaticAndDefaultsAction, GetParamsStaticAndDefaultsResult> {

    private final FundDao fundDao;

    @Inject
    public GetParamsStaticAndDefaultHandler(FundDao fundDao) {
        this.fundDao = fundDao;
    }

    @Override
    public GetParamsStaticAndDefaultsResult execute(GetParamsStaticAndDefaultsAction action, ExecutionContext context) throws ActionException {

        BigDecimal investmentAmountDefault = new BigDecimal("10000");
        BigDecimal transactionCostDefault = new BigDecimal("12.95");
        //TODO: retrieve calculated ranges from database
        List<Integer> performanceRangeAvailable = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        Integer performanceRangeDefault = 9;
        List<Integer> rebalanceFrequencyAvailable = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        Integer rebalanceFrequencyDefault = 1;
        List<Integer> portfolioSizeAvailable = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        Integer portfolioSizeDefault = 3;
        Integer volatilityFilterDefault = 20;
        List<String> providers = fundDao.getProviders();
        List<String> categories = fundDao.getCategories();
        //TODO: retrieve earliest date from database
        Date fromDate = new Date(2000, 1, 1);
        Date toDate = new Date();

        String errorText = "";
        return new GetParamsStaticAndDefaultsResult(errorText, investmentAmountDefault, transactionCostDefault,
                performanceRangeAvailable, performanceRangeDefault, rebalanceFrequencyAvailable, rebalanceFrequencyDefault,
                portfolioSizeAvailable, portfolioSizeDefault, volatilityFilterDefault, providers, categories,
                fromDate, toDate);
    }

    @Override
    public Class<GetParamsStaticAndDefaultsAction> getActionType() {
        return GetParamsStaticAndDefaultsAction.class;
    }

    @Override
    public void undo(GetParamsStaticAndDefaultsAction action, GetParamsStaticAndDefaultsResult result, ExecutionContext context) throws ActionException {
        throw new UnsupportedOperationException();
    }
}
