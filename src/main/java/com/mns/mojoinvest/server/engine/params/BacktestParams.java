package com.mns.mojoinvest.server.engine.params;

import com.mns.mojoinvest.server.CustomDateDeserializer;
import com.mns.mojoinvest.server.CustomDateSerializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

public class BacktestParams {

//      ERROR: Errors in 'file:/Users/marknuttallsmith/Projects/Alpha-Position/src/main/java/com/mns/mojoinvest/shared/params/BacktestParams.java'.
//    ERROR: Line 10: No source code is available for type org.joda.time.LocalDate; did you forget to inherit a required module?.

    //probably best to only pass java.util.Date to and from client - can adapt on way in and out?
    //TODO: Now we're no longer using gwt it should be possible to serialise directly to jodatime

    private Date fromDate;

    private Date toDate;


    public BacktestParams(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public BacktestParams() {
        //For serialization
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public Date getFromDate() {
        return fromDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public Date getToDate() {
        return toDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "BacktestParams: {" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                '}';
    }

}
