package com.mns.alphaposition.server.engine.transaction;

import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class AbstractTransaction implements Transaction {

    protected final String ref;
    protected final String symbol;
    protected final DateMidnight date;
    protected final BigDecimal units;
    protected final BigDecimal price;
    protected final BigDecimal commission;

    protected final static DateTimeFormatter fmt = DateTimeFormat.forPattern("E dd MMM yyyy");


    public AbstractTransaction(String ref, String symbol, DateMidnight date, BigDecimal units,
                               BigDecimal price, BigDecimal commission) {
        this.ref = ref;
        this.symbol = symbol;
        this.date = date;
        this.units = units;
        this.price = price;
        this.commission = commission;
    }

    public String getRef() {
        return ref;
    }

    public String getSymbol() {
        return symbol;
    }

    public DateMidnight getDate() {
        return date;
    }

    public BigDecimal getUnits() {
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
        return symbol + '\'' +
                ", units=" + units +
                ", price=" + price.setScale(2, RoundingMode.HALF_UP) +
                ", commission=" + commission +
                ", date=" + fmt.print(date);
    }

    public int compareTo(Object o) {
        Transaction otherTransaction = (Transaction) o;
        return date.compareTo(otherTransaction.getDate());
    }
    
}
