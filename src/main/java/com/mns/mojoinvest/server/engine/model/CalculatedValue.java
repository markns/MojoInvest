package com.mns.mojoinvest.server.engine.model;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;
import org.joda.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Cached
@Entity
@Unindexed
public class CalculatedValue {

    @Id
    private String key;

    private BigDecimal value;

    public CalculatedValue() {
    }

    public CalculatedValue(String key, BigDecimal value) {
        this.key = key;
        this.value = value;
    }

    public CalculatedValue(LocalDate date, String symbol, String type, int period, BigDecimal value) {
        this.key = date + "|" + symbol + "|" + type + "|" + period;
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CalculatedValue{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }

    public static CalculatedValue fromStrArr(String[] row) {
        return new CalculatedValue(row[0], new BigDecimal(row[1]));
    }

    public String[] toStrArr() {
        return new String[]{
                key,
                value + ""};
    }
}
