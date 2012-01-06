package com.mns.mojoinvest.shared.dispatch;

import com.gwtplatform.dispatch.annotation.GenDispatch;
import com.gwtplatform.dispatch.annotation.In;
import com.gwtplatform.dispatch.annotation.Out;
import com.mns.mojoinvest.shared.params.BacktestParams;
import com.mns.mojoinvest.shared.params.MomentumStrategyParams;
import com.mns.mojoinvest.shared.params.PortfolioParams;

@GenDispatch(isSecure = false)
public class RunStrategy  {

    @In(1)
    PortfolioParams portfolioParams;

    @In(2)
    MomentumStrategyParams strategyParams;

    @In(3)
    BacktestParams backtestParams;

    @Out(1)
    String errorText; // empty if success

//	@Out(2)
//    List<String> providersAvailable;

}
