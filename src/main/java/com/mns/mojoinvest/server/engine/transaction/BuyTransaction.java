package com.mns.mojoinvest.server.engine.transaction;

import org.joda.time.LocalDate;

import java.math.BigDecimal;

public class BuyTransaction extends AbstractTransaction {

    public BuyTransaction(String fund, LocalDate date, BigDecimal units,
                          BigDecimal price, BigDecimal commission) {
        super(fund, date, units, price, commission);
    }

    public BigDecimal getInitialInvestment() {
        return units.multiply(price).add(commission);
    }

    public BigDecimal getCashValue() {
        return getInitialInvestment().negate();
    }


}
