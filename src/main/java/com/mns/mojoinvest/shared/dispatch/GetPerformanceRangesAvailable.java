package com.mns.mojoinvest.shared.dispatch;

import com.gwtplatform.dispatch.annotation.GenDispatch;
import com.gwtplatform.dispatch.annotation.Out;

import java.util.ArrayList;

@GenDispatch(isSecure = false)
public class GetPerformanceRangesAvailable {

    @Out(1)
    String errorText; // empty if success

    @Out(2)
    ArrayList<Integer> performanceRangesAvailable;

}
