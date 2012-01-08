package com.mns.mojoinvest.shared.dispatch;

import com.gwtplatform.dispatch.annotation.GenDispatch;
import com.gwtplatform.dispatch.annotation.In;
import com.gwtplatform.dispatch.annotation.Out;
import com.mns.mojoinvest.shared.dto.StrategyResult;
import com.mns.mojoinvest.shared.params.Params;

@GenDispatch(isSecure = false)
public class RunStrategy  {

    @In(1)
    Params params;

    @Out(1)
    String errorText; // empty if success

	@Out(2)
    StrategyResult strategyResult;

}
