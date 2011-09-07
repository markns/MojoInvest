package com.mns.alphaposition.server.engine.transaction;

import com.mns.alphaposition.server.engine.model.Fund;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

public class SellTransaction extends AbstractTransaction {

    public SellTransaction(Fund fund, LocalDate date, BigDecimal units,
                          BigDecimal price, BigDecimal commission) {
        super(fund, date, units, price, commission);
    }

    public BigDecimal getCashValue() {
        return units.multiply(price).subtract(commission);
    }

}
