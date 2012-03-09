package com.mns.mojoinvest.server.handler;

import com.google.inject.Inject;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.shared.dispatch.GetCategoriesAvailableAction;
import com.mns.mojoinvest.shared.dispatch.GetCategoriesAvailableResult;

import java.util.HashSet;

public class GetCategoriesAvailableHandler implements
        ActionHandler<GetCategoriesAvailableAction, GetCategoriesAvailableResult> {

    private final FundDao fundDao;

    @Inject
    public GetCategoriesAvailableHandler(FundDao fundDao) {
        this.fundDao = fundDao;
    }

    @Override
    public GetCategoriesAvailableResult execute(GetCategoriesAvailableAction action, ExecutionContext context) throws ActionException {
        return new GetCategoriesAvailableResult("", new HashSet<String>(fundDao.getCategorySet()));
    }

    @Override
    public Class<GetCategoriesAvailableAction> getActionType() {
        return GetCategoriesAvailableAction.class;
    }

    @Override
    public void undo(GetCategoriesAvailableAction action, GetCategoriesAvailableResult result, ExecutionContext context) throws ActionException {
        throw new UnsupportedOperationException();
    }
}
