package com.mns.mojoinvest.server.engine.transaction;

import com.mns.mojoinvest.server.engine.model.Fund;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

public abstract class AbstractTransaction implements Transaction {

    protected final String ref;
    protected final Fund fund;
    protected final LocalDate date;
    protected final BigDecimal units;
    protected final BigDecimal price;
    protected final BigDecimal commission;

    public AbstractTransaction(Fund fund, LocalDate date, BigDecimal units,
                               BigDecimal price, BigDecimal commission) {
        this.ref = fund.getSymbol() + "/" + date;
        this.fund = fund;
        this.date = date;
        this.units = units;
        this.price = price;
        this.commission = commission;
    }

    public String getRef() {
        return ref;
    }

    public Fund getFund() {
        return fund;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getQuantity() {
        return units;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                "{fund=" + fund.getSymbol() +
                ", date=" + date +
                ", units=" + units +
                ", price=" + price +
                ", commission=" + commission +
                '}';
    }
}
