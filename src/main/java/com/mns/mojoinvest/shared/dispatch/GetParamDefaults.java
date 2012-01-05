package com.mns.mojoinvest.shared.dispatch;

import com.gwtplatform.dispatch.annotation.GenDispatch;
import com.gwtplatform.dispatch.annotation.Out;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

@GenDispatch(isSecure = false)
public class GetParamDefaults {

    @Out(1)
    String errorText; // empty if success

	@Out(2)
    BigDecimal investmentAmountDefault;

	@Out(3)
    BigDecimal transactionCostDefault;

    @Out(4)
    Integer performanceRangeDefault;

    @Out(5)
    Integer rebalanceFrequencyDefault;

    @Out(6)
    Integer portfolioSizeDefault;

    @Out(7)
    Integer volatilityFilterDefault;

    @Out(8)
    HashMap<String, Boolean> providers;

    @Out(9)
    HashMap<String, Boolean> categories;

    @Out(10)
    Date fromDate;

    @Out(11)
    Date toDate;

}
