package com.mns.mojoinvest.shared.dispatch;

import com.gwtplatform.dispatch.annotation.GenDispatch;
import com.gwtplatform.dispatch.annotation.In;
import com.gwtplatform.dispatch.annotation.Out;
import com.mns.mojoinvest.shared.dto.DataTableDto;

@GenDispatch(isSecure = false)
public class GetFundPerformance {

    @In(1)
	String symbol;

    @Out(1)
    String errorText; // empty if success

	@Out(2)
    DataTableDto dataTableDto;

}
