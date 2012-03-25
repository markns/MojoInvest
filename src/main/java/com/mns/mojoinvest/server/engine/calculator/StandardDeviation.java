package com.mns.mojoinvest.server.engine.calculator;

public class StandardDeviation {

    public static double StandardDeviationMean(double[] data) {
        // sd is sqrt of sum of (values-mean) squared divided by n - 1
        // Calculate the mean
        double mean = 0;
        final int n = data.length;
        if (n < 2) {
            return Double.NaN;
        }
        for (int i = 0; i < n; i++) {
            mean += data[i];
        }
        mean /= n;
        // calculate the sum of squares
        double sum = 0;
        for (int i = 0; i < n; i++) {
            final double v = data[i] - mean;
            sum += v * v;
        }
        // Change to ( n - 1 ) to n if you have complete data instead of a sample.
        return Math.sqrt(sum / (n - 1));
    }

    public static double standardDeviationCalculate(double[] data) {
        final int n = data.length;
        if (n < 2) {
            return Double.NaN;
        }
        double avg = data[0];
        double sum = 0;
        for (int i = 1; i < data.length; i++) {
            double newavg = avg + (data[i] - avg) / (i + 1);
            sum += (data[i] - avg) * (data[i] - newavg);
            avg = newavg;
        }
        // Change to ( n - 1 ) to n if you have complete data instead of a sample.
        return Math.sqrt(sum / (n - 1));
    }

    public static void main(String[] args) {
        double[] data = {10, 100, 50};
        System.out.println(StandardDeviationMean(data));
        System.out.println(standardDeviationCalculate(data));
    }
} 