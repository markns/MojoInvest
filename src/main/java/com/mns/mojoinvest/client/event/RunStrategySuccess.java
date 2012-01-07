package com.mns.mojoinvest.client.event;

import com.gwtplatform.dispatch.annotation.GenEvent;
import com.gwtplatform.dispatch.annotation.Order;
import com.mns.mojoinvest.shared.dispatch.RunStrategyResult;

@GenEvent
public class RunStrategySuccess {

    @Order(1)
    RunStrategyResult runStrategyResult;

    @Order(2)
    boolean originator;

}
