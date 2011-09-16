package com.mns.alphaposition.shared.params;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;
import java.util.Map;

public class BackTestParams implements IsSerializable{

//      ERROR: Errors in 'file:/Users/marknuttallsmith/Projects/Alpha-Position/src/main/java/com/mns/alphaposition/shared/params/BackTestParams.java'.
//    ERROR: Line 10: No source code is available for type org.joda.time.LocalDate; did you forget to inherit a required module?.

//probably best to only pass java.util.Date to and from client - can adapt on way in and out?
    private Date fromDate;

    private Date toDate;

    private Map<String, String> fundsFilter;

    private StrategyParams strategyParams;

    public BackTestParams() {
    }

    public BackTestParams(Date fromDate, Date toDate,
                          Map<String, String> fundsFilter, StrategyParams strategyParams) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fundsFilter = fundsFilter;
        this.strategyParams = strategyParams;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public Map<String, String> getFundsFilter() {
        return fundsFilter;
    }

    public StrategyParams getStrategyParams() {
        return strategyParams;
    }
}
