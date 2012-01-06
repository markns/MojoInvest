package com.mns.mojoinvest.shared.params;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;
import java.util.List;

public class BacktestParams implements IsSerializable{

//      ERROR: Errors in 'file:/Users/marknuttallsmith/Projects/Alpha-Position/src/main/java/com/mns/mojoinvest/shared/params/BacktestParams.java'.
//    ERROR: Line 10: No source code is available for type org.joda.time.LocalDate; did you forget to inherit a required module?.

//probably best to only pass java.util.Date to and from client - can adapt on way in and out?
    private Date fromDate;

    private Date toDate;

    private List<String> providers;

    private List<String> categories;


    public BacktestParams(Date fromDate, Date toDate, List<String> providers, List<String> categories) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.providers = providers;
        this.categories = categories;
    }

    public BacktestParams() {
        //For serialization
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public List<String> getProviders() {
        return providers;
    }

    public List<String> getCategories() {
        return categories;
    }
}
