package com.mns.alphaposition.server.engine.portfolio;

import com.mns.alphaposition.server.engine.transaction.BuyTransaction;
import com.mns.alphaposition.server.engine.transaction.Transaction;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Long Lot Computations
 * <p/>
 * A simple lot (sometimes called a long lot) consists of a stock purchase, possibly followed
 * by stock sales. Each lot maintains three fundamental values that affect all the remaining
 * calculations:
 * <p/>
 * * initial quantity:   This is the share count of the transaction that opened the lot.
 * * remaining quantity: This is the number of shares that have not been matched with
 * subsequent sell transactions.
 * * initial investment: This is the negative of the cash value of the transaction that
 * opened the lot, because the cash value is the effect on your
 * bank account, but the initial investment is the opposite: it
 * is the value that has been "put into" the stock. So, for example,
 * the cash value of a purchase of 10 shares of GOOG at $350 is -$3500,
 * and the initial investment is $3500.
 * <p/>
 * From these values, the following investment statistics can be computed for the lot:
 * <p/>
 * cost basis: cost basis = initial investment * (remaining quantity / initial quantity)
 * market value: market value = remaining quantity * share price
 * gain: gain = market value - cost basis [Note that this might be negative!]
 * todays's gain: today's gain = remaining quantity * price change
 * gain percentage: gain percentage = gain / cost basis
 */

public class Lot {

    private final Logger logger = Logger.getLogger(Lot.class);

    /**
     * Share count of the transaction that opened the lot
     */
    private final BigDecimal initialQuantity;

    /**
     * Negative of the cash value of the transaction that opened the lot
     */
    private final BigDecimal initialInvestment;

    /**
     * Number of shares that have not been matched with subsequent sell transactions
     */
    private BigDecimal remainingQuantity;

    private Transaction openingTransaction;

    private List<Transaction> closingTransactions;

    public Lot(BuyTransaction openingTransaction) {
        this.initialQuantity = openingTransaction.getUnits();
        this.initialInvestment = openingTransaction.getInitialInvestment();
        this.remainingQuantity = initialQuantity;
        this.openingTransaction = openingTransaction;
        this.closingTransactions = new ArrayList<Transaction>();
    }


    public boolean addClosingTransaction(Transaction transaction) {
        return closingTransactions.add(transaction);
    }

    public BigDecimal getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(BigDecimal remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public BigDecimal costBasis() {
        return initialInvestment.add(openingTransaction.getCommission())
                .multiply(remainingQuantity.divide(initialQuantity, MathContext.DECIMAL32
));
    }

    public BigDecimal marketValue(BigDecimal sharePrice) {
        return remainingQuantity.multiply(sharePrice);
    }

    public BigDecimal gain(BigDecimal sharePrice) {
        return marketValue(sharePrice).subtract(costBasis());
    }

    public BigDecimal todaysGain(BigDecimal priceChange) {
        return remainingQuantity.multiply(priceChange);
    }

    public BigDecimal gainPercentage(BigDecimal sharePrice) {
        return gain(sharePrice).divide(costBasis(), MathContext.DECIMAL32);
    }

    public BigDecimal returnsGain(BigDecimal sharePrice) {
        //returns gain = market_value + cash in - cash out.
        return marketValue(sharePrice).add(cashIn().add(cashOut()));
    }

    public BigDecimal cashOut() {
        return openingTransaction.getCashValue();
    }

    public BigDecimal cashIn() {
        BigDecimal cashIn = BigDecimal.ZERO;
        for (Transaction transaction : closingTransactions) {
            cashIn = cashIn.add(transaction.getCashValue());
        }
        return cashIn;
    }

    public BigDecimal overallReturn(BigDecimal sharePrice) {
        //Overall return = returns gain / initial investment
        return returnsGain(sharePrice).divide(initialInvestment, MathContext.DECIMAL32
);
    }

    public boolean closed() {
        return remainingQuantity.compareTo(BigDecimal.ZERO) == 0;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
