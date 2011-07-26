package com.mns.alphaposition.server.engine.transaction;

import org.joda.time.DateMidnight;

import java.math.BigDecimal;

public class SellTransaction extends AbstractTransaction {

    public SellTransaction(String ref, String symbol, DateMidnight date, BigDecimal units,
                           BigDecimal price, BigDecimal commission) {
        super(ref, symbol, date, units, price, commission);
    }

    public BigDecimal getCashValue() {
        return units.multiply(price).subtract(commission);
    }

}
