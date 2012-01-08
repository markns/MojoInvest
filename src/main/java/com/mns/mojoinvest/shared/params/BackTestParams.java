package com.mns.mojoinvest.shared.params;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;

public class BacktestParams implements IsSerializable {

//      ERROR: Errors in 'file:/Users/marknuttallsmith/Projects/Alpha-Position/src/main/java/com/mns/mojoinvest/shared/params/BacktestParams.java'.
//    ERROR: Line 10: No source code is available for type org.joda.time.LocalDate; did you forget to inherit a required module?.

    //probably best to only pass java.util.Date to and from client - can adapt on way in and out?
    private Date fromDate;

    private Date toDate;


    public BacktestParams(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public BacktestParams() {
        //For serialization
    }

    public Date getFromDate() {
        return fromDate;
    }



    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "BacktestParams{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                '}';
    }
}
