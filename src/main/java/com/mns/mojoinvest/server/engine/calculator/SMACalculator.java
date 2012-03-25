package com.mns.mojoinvest.server.engine.calculator;

import java.math.BigDecimal;
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
        if (window.isEmpty()) return BigDecimal.ZERO; // technically the average is undefined
        return new BigDecimal(sum / window.size());
    }

    public static void main(String[] args) {
        double[] testData = {1, 2, 3, 4, 5, 5, 4, 3, 2, 1};
        int[] windowSizes = {3, 5};
        for (int windSize : windowSizes) {
            SMACalculator SMA = new SMACalculator(windSize);
            for (double x : testData) {
                SMA.newNum(new BigDecimal(x));
                System.out.println("Next number = " + x + ", SMA = " + SMA.getAvg());
            }
            System.out.println();
        }
    }
}
