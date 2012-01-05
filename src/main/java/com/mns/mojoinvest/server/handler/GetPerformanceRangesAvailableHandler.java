package com.mns.mojoinvest.server.handler;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.mojoinvest.shared.dispatch.GetPerformanceRangesAvailableAction;
import com.mns.mojoinvest.shared.dispatch.GetPerformanceRangesAvailableResult;

import java.util.Arrays;
import java.util.List;

public class GetPerformanceRangesAvailableHandler implements
        ActionHandler<GetPerformanceRangesAvailableAction, GetPerformanceRangesAvailableResult> {

    @Override
    public GetPerformanceRangesAvailableResult execute(GetPerformanceRangesAvailableAction action, ExecutionContext context) throws ActionException {

        List<Integer> performanceRangeAvailable = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

        return new GetPerformanceRangesAvailableResult("", performanceRangeAvailable);
    }

    @Override
    public Class<GetPerformanceRangesAvailableAction> getActionType() {
        return GetPerformanceRangesAvailableAction.class;
    }

    @Override
    public void undo(GetPerformanceRangesAvailableAction action, GetPerformanceRangesAvailableResult result, ExecutionContext context) throws ActionException {
        throw new UnsupportedOperationException();
    }
}
