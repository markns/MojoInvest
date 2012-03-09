package com.mns.mojoinvest.server.handler;

import com.google.inject.Inject;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.shared.dispatch.GetProvidersAvailableAction;
import com.mns.mojoinvest.shared.dispatch.GetProvidersAvailableResult;

import java.util.HashSet;

public class GetProvidersAvailableHandler implements
        ActionHandler<GetProvidersAvailableAction, GetProvidersAvailableResult> {

    private final FundDao fundDao;

    @Inject
    public GetProvidersAvailableHandler(FundDao fundDao) {
        this.fundDao = fundDao;
    }

    @Override
    public GetProvidersAvailableResult execute(GetProvidersAvailableAction action, ExecutionContext context) throws ActionException {
        return new GetProvidersAvailableResult("", new HashSet<String>(fundDao.getProviderSet()));
    }

    @Override
    public Class<GetProvidersAvailableAction> getActionType() {
        return GetProvidersAvailableAction.class;
    }

    @Override
    public void undo(GetProvidersAvailableAction action, GetProvidersAvailableResult result, ExecutionContext context) throws ActionException {
        throw new UnsupportedOperationException();
    }
}
