package com.mns.alphaposition.server.engine.transaction;

import org.joda.time.DateMidnight;

import java.math.BigDecimal;

public class BuyTransaction extends AbstractTransaction {

    public BuyTransaction(String ref, String symbol, DateMidnight date, BigDecimal units,
                          BigDecimal price, BigDecimal commission) {
        super(ref, symbol, date, units, price, commission);
    }

    public BigDecimal getInitialInvestment() {
        return units.multiply(price); 
    }

    public BigDecimal getCashValue() {
        return units.multiply(price).add(commission).negate();
    }



}
