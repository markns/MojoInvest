package com.mns.mojoinvest.server.engine.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedList;
import java.util.Queue;

public class SMACalculator {

    private final Queue<Double> window = new LinkedList<Double>();
    private final int period;
    private double sum;

    public SMACalculator(int period) {
        assert period > 0 : "Period must be a positive integer";
        this.period = period;
    }

    public void newNum(BigDecimal num) {
        sum += num.doubleValue();
        window.add(num.doubleValue());
        if (window.size() > period) {
            sum -= window.remove();
        }
    }

    public BigDecimal getAvg() {
        if (window.size() != period) return null; // technically the average is undefined
        return new BigDecimal(sum / window.size(), MathContext.DECIMAL32);
    }


}
