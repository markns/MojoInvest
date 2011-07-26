package com.mns.alphaposition.server.handler;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.alphaposition.shared.action.RunBackTestAction;
import com.mns.alphaposition.shared.action.RunBackTestResult;

import com.google.inject.Inject;

public class RunBackTestHandler implements ActionHandler<RunBackTestAction, RunBackTestResult> {




    @Inject
    //Map of strategies

    @Override
    public RunBackTestResult execute(RunBackTestAction runBackTestAction, ExecutionContext executionContext)
            throws ActionException {

        //look up strategy in map
        //strategy.execute

        return null;
    }

    @Override
    public Class<RunBackTestAction> getActionType() {
        return RunBackTestAction.class;
    }

    @Override
    public void undo(RunBackTestAction runBackTestAction, RunBackTestResult runBackTestResult,
                     ExecutionContext executionContext) throws ActionException {


    }
}
