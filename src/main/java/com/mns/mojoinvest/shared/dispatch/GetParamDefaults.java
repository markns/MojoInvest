package com.mns.mojoinvest.shared.dispatch;

import com.gwtplatform.dispatch.annotation.GenDispatch;
import com.gwtplatform.dispatch.annotation.Out;
import com.mns.mojoinvest.shared.params.Params;

@GenDispatch(isSecure = false)
public class GetParamDefaults {

    @Out(1)
    String errorText; // empty if success

//	@Out(2)
//    BigDecimal investmentAmountDefault;
//
//	@Out(3)
//    BigDecimal transactionCostDefault;
//
//    @Out(4)
//    Integer performanceRangeDefault;
//
//    @Out(5)
//    Integer rebalanceFrequencyDefault;
//
//    @Out(6)
//    Integer portfolioSizeDefault;
//
//    @Out(7)
//    Integer volatilityFilterDefault;
//
//    @Out(8)
//    HashMap<String, Boolean> providers;
//
//    @Out(9)
//    HashMap<String, Boolean> categories;
//
//    @Out(10)
//    Date fromDate;
//
//    @Out(11)
//    Date toDate;

    @Out(2)
    Params params;

//    @Out(2)
//    PortfolioParams portfolioParams;
//
//    @Out(3)
//    BacktestParams backtestParams;
//
//    @Out(4)
//    MomentumStrategyParams strategyParams;




}
