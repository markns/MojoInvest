package com.mns.alphaposition.server.handler;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.alphaposition.shared.action.RunBackTestAction;
import com.mns.alphaposition.shared.action.RunBackTestResult;

public class RunBackTestHandler implements ActionHandler<RunBackTestAction, RunBackTestResult> {


    @Override
    public RunBackTestResult execute(RunBackTestAction runBackTestAction, ExecutionContext executionContext) throws ActionException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class<RunBackTestAction> getActionType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void undo(RunBackTestAction runBackTestAction, RunBackTestResult runBackTestResult, ExecutionContext executionContext) throws ActionException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
