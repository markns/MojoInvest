package com.mns.mojoinvest.server.engine.transaction;

import org.joda.time.LocalDate;

import java.math.BigDecimal;

public class SellTransaction extends AbstractTransaction {

    public SellTransaction(String fund, LocalDate date, BigDecimal units,
                           BigDecimal price, BigDecimal commission) {
        super(fund, date, units, price, commission);
    }

    @Override
    public String getType() {
        return "Sell";
    }

    public BigDecimal getCashValue() {
        return units.multiply(price).subtract(commission);
    }

}
