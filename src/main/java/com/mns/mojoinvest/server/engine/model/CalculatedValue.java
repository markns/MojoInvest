package com.mns.mojoinvest.server.engine.model;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.logging.Logger;

public class CalculatedValue {

    private static final Logger log = Logger.getLogger(CalculatedValue.class.getName());

    private BigDecimal value;

    private String dateStr;
    private LocalDate date;
    private String symbol;
    private String type;
    private int period;


    public CalculatedValue(String date, String symbol, String type, int period, BigDecimal value) {

        this.dateStr = date;
        this.symbol = symbol;
        this.type = type;
        this.period = period;
        this.value = value;
    }

    public CalculatedValue(LocalDate date, String symbol, String type, int period, double value) {
        this.date = date;
        this.symbol = symbol;
        this.type = type;
        this.period = period;
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            this.value = BigDecimal.ZERO;
        } else {
            this.value = new BigDecimal(value, MathContext.DECIMAL32).setScale(3, RoundingMode.HALF_EVEN);
        }
    }


    public LocalDate getDate() {
        if (date == null)
            date = new LocalDate(dateStr);
        return date;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getType() {
        return type;
    }

    public int getPeriod() {
        return period;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CalculatedValue{" +
                "key='" + getKey() + '\'' +
                ", value=" + value +
                '}';
    }

    private String key;

    public String getKey() {
        if (key == null)
            key = type + "|" + period;
        return key;
    }


}
