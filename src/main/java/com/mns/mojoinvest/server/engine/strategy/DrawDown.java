package com.mns.mojoinvest.server.engine.strategy;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;

public class DrawDown {
    private LocalDate maxDate;
    private BigDecimal max;
    private LocalDate minDate;
    private BigDecimal min;

    public DrawDown(LocalDate maxDate, BigDecimal max) {
        this.maxDate = maxDate;
        this.max = max;
    }

    public LocalDate getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(LocalDate maxDate) {
        this.maxDate = maxDate;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public LocalDate getMinDate() {
        return minDate;
    }

    public void setMinDate(LocalDate minDate) {
        this.minDate = minDate;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    private static BigDecimal HUNDRED = new BigDecimal("100");

    public BigDecimal getPctValue() {
        BigDecimal change = max.subtract(min);
        return change.divide(max, MathContext.DECIMAL32).multiply(HUNDRED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrawDown drawDown = (DrawDown) o;

        if (max != null ? !max.equals(drawDown.max) : drawDown.max != null) return false;
        if (maxDate != null ? !maxDate.equals(drawDown.maxDate) : drawDown.maxDate != null) return false;
        if (min != null ? !min.equals(drawDown.min) : drawDown.min != null) return false;
        if (minDate != null ? !minDate.equals(drawDown.minDate) : drawDown.minDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = maxDate != null ? maxDate.hashCode() : 0;
        result = 31 * result + (max != null ? max.hashCode() : 0);
        result = 31 * result + (minDate != null ? minDate.hashCode() : 0);
        result = 31 * result + (min != null ? min.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DrawDown{" +
                "maxDate=" + maxDate +
                ", max=" + max +
                ", minDate=" + minDate +
                ", min=" + min +
                '}';
    }
}
