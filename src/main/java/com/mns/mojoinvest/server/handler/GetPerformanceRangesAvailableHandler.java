package com.mns.mojoinvest.server.handler;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.mojoinvest.shared.dispatch.GetPerformanceRangesAvailableAction;
import com.mns.mojoinvest.shared.dispatch.GetPerformanceRangesAvailableResult;

import java.util.ArrayList;
import java.util.Arrays;

//TODO: refactor to GetFormationPeriodsAvailableHandler
public class GetPerformanceRangesAvailableHandler implements
        ActionHandler<GetPerformanceRangesAvailableAction, GetPerformanceRangesAvailableResult> {

    @Override
    public GetPerformanceRangesAvailableResult execute(GetPerformanceRangesAvailableAction action, ExecutionContext context) throws ActionException {

        //TODO: Load formation periods available from database
        ArrayList<Integer> performanceRangeAvailable = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));

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
