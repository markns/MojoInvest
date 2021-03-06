package com.mns.mojoinvest.server.engine.result;

import java.util.Date;

public class TransactionDto {

    private String buySell;
    private String symbol;
    private String fundName;
    private Date date;
    private Double units;
    private Double price;
    private Double commission;

    public TransactionDto(String buySell, String symbol, String fundName, Date date,
                          Double units, Double price, Double commission) {
        this.buySell = buySell;
        this.symbol = symbol;
        this.fundName = fundName;
        this.date = date;
        this.units = units;
        this.price = price;
        this.commission = commission;
    }

    public TransactionDto() {
        //For serialization
    }

    public String getBuySell() {
        return buySell;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getFundName() {
        return fundName;
    }

    public Date getDate() {
        return date;
    }

    public Double getUnits() {
        return units;
    }

    public Double getPrice() {
        return price;
    }

    public Double getCommission() {
        return commission;
    }
}
