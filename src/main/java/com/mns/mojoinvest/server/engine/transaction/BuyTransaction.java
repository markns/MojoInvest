package com.mns.mojoinvest.server.engine.transaction;

import com.mns.mojoinvest.server.engine.model.Fund;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

public class BuyTransaction extends AbstractTransaction {

    public BuyTransaction(Fund fund, LocalDate date, BigDecimal units,
                          BigDecimal price, BigDecimal commission) {
        super(fund, date, units, price, commission);
    }

    public BigDecimal getInitialInvestment() {
        return units.multiply(price); 
    }

    public BigDecimal getCashValue() {
        return units.multiply(price).add(commission).negate();
    }



}
