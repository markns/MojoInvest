package com.mns.mojoinvest.server.engine.params;


import com.mns.mojoinvest.server.serialization.CustomDateDeserializer;
import com.mns.mojoinvest.server.serialization.CustomDateSerializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

public class PortfolioParams {

    private Date creationDate;
    private Double initialInvestment;
    private Double transactionCost;

    public PortfolioParams(Double initialInvestment, Double transactionCost, Date creationDate) {
        this.initialInvestment = initialInvestment;
        this.transactionCost = transactionCost;
        this.creationDate = creationDate;
    }

    public Double getInitialInvestment() {
        return initialInvestment;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public Date getCreationDate() {
        return creationDate;
    }

    public void setInitialInvestment(Double initialInvestment) {
        this.initialInvestment = initialInvestment;
    }

    public Double getTransactionCost() {
        return transactionCost;
    }

    public void setTransactionCost(Double transactionCost) {
        this.transactionCost = transactionCost;
    }

    @Override
    public String toString() {
        return "PortfolioParams{" +
                "initialInvestment=" + initialInvestment +
                ", transactionCost=" + transactionCost +
                '}';
    }
}
