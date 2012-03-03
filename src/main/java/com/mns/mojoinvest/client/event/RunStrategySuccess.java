package com.mns.mojoinvest.client.event;

import com.gwtplatform.dispatch.annotation.Order;
import com.mns.mojoinvest.shared.dispatch.RunStrategyResult;

//@GenEvent TODO: Figure out why this GenEvent screws up - something to do with the dependency on RunStrategyResult
public class RunStrategySuccess {

    @Order(1)
    RunStrategyResult runStrategyResult;

    @Order(2)
    boolean originator;

}
