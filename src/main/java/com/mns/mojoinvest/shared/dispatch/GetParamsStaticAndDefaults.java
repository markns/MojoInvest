package com.mns.mojoinvest.shared.dispatch;

import com.gwtplatform.dispatch.annotation.GenDispatch;
import com.gwtplatform.dispatch.annotation.Out;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@GenDispatch(isSecure = false)
public class GetParamsStaticAndDefaults {

    @Out(1)
    String errorText; // empty if success

	@Out(2)
    BigDecimal investmentAmountDefault;

	@Out(3)
    BigDecimal transactionCostDefault;

    @Out(4)
    List<Integer> performanceRangeAvailable;

    @Out(5)
    Integer performanceRangeDefault;

    @Out(6)
    List<Integer> rebalanceFrequencyAvailable;

    @Out(7)
    Integer rebalanceFrequencyDefault;

    @Out(8)
    List<Integer> portfolioSizeAvailable;

    @Out(9)
    Integer portfolioSizeDefault;

    @Out(10)
    Integer volatilityFilterDefault;

    @Out(11)
    List<String> providers;

    @Out(12)
    List<String> categories;

    @Out(13)
    Date fromDate;

    @Out(14)
    Date toDate;

}
