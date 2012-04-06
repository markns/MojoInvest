package com.mns.mojoinvest.server.engine.transaction;

import org.joda.time.LocalDate;

import java.math.BigDecimal;

public abstract class AbstractTransaction implements Transaction {

    protected final String ref;
    protected final String fund;
    protected final LocalDate date;
    protected final BigDecimal units;
    protected final BigDecimal price;
    protected final BigDecimal commission;

    public AbstractTransaction(String fund, LocalDate date, BigDecimal units,
                               BigDecimal price, BigDecimal commission) {
        this.ref = fund + "/" + date;
        this.fund = fund;
        this.date = date;
        this.units = units;
        this.price = price;
        this.commission = commission;
    }

    public String getRef() {
        return ref;
    }

    public String getFund() {
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
        return this.getClass().getSimpleName().replace("Transaction", "") +
                "{fund=" + fund +
                ", date=" + date +
                ", units=" + units +
                ", price=" + price +
                ", commission=" + commission +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractTransaction that = (AbstractTransaction) o;

        if (!commission.equals(that.commission)) return false;
        if (!date.equals(that.date)) return false;
        if (!fund.equals(that.fund)) return false;
        if (!price.equals(that.price)) return false;
        if (!ref.equals(that.ref)) return false;
        if (!units.equals(that.units)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ref.hashCode();
        result = 31 * result + fund.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + units.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + commission.hashCode();
        return result;
    }

    public static Transaction fromStrArr(String[] arr) {
        if (arr[0].equals("BUY")) {
            return new BuyTransaction(arr[1],
                    new LocalDate(arr[2]),
                    new BigDecimal(arr[3]),
                    new BigDecimal(arr[4]),
                    new BigDecimal(arr[5])
            );
        } else {
            return new SellTransaction(arr[1],
                    new LocalDate(arr[2]),
                    new BigDecimal(arr[3]),
                    new BigDecimal(arr[4]),
                    new BigDecimal(arr[5]));
        }
    }

    public String[] toStrArr() {
        String buySell = this instanceof BuyTransaction ? "BUY" : "SELL";
        return new String[]{
                buySell,
                fund,
                date.toString(),
                units.toString(),
                price.toString(),
                commission.toString()
        };
    }

}
