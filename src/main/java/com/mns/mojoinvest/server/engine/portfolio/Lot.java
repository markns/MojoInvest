package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.server.engine.transaction.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
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


    private BuyTransaction openingTransaction;

    private List<SellTransaction> closingTransactions;

    public Lot(BuyTransaction openingTransaction) {
        this.openingTransaction = openingTransaction;
        this.closingTransactions = new ArrayList<SellTransaction>();
    }


    public boolean addClosingTransaction(SellTransaction transaction) {
        return closingTransactions.add(transaction);
    }

    public BigDecimal getRemainingQuantity() {
        BigDecimal closingQuantity = BigDecimal.ZERO;
        for (SellTransaction closingTransaction : closingTransactions) {
            closingQuantity = closingQuantity.add(closingTransaction.getQuantity());
        }
        return getInitialQuantity().subtract(closingQuantity);
    }

//    public void setRemainingQuantity(BigDecimal remainingQuantity) {
//        this.remainingQuantity = remainingQuantity;
//    }

    public boolean closed() {
        return getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0;
    }

    public BigDecimal getInitialInvestment() {
        return openingTransaction.getInitialInvestment()
                .add(openingTransaction.getCommission());
    }

    public BigDecimal getInitialQuantity() {
        return openingTransaction.getQuantity();
    }

    /*   Cost Basis

       We should linger on Cost basis for a moment. When Google Finance presents summary statistics for your portfolio,
       it computes those numbers based on the number of shares you still own. So, if you bought 100 shares of GOOG,
       then sold them all, your cost basis will be reported as 0, as will your market value, gain, etc. If you only
       sold 25 shares, then all of your statistics will be based on the 75 shares that you still own. Notice that since
       this ratio is applied to your entire initial investment, which includes commission costs, only the portion of
       your commission costs that applied to the stocks you still own will be considered.

       Some examples:
       Suppose you buy 100 shares of GOOG on April 1, 2008:
       Transaction: 4/1/2008 BUY GOOG 100 @ $471.09 ($15 commission) -> the cost basis is 100 * 471.09 + 15 = $47.124.00
       Now Suppose you buy 100 shares of GOOG on April 1, 2008, but sell 50 on 5/5/2008:
       Transaction: 4/1/2008 BUY GOOG 100 @ $471.09 ($15 commission) -> At this point you own 100 shares.
       Transaction: 5/5/2008 SELL GOOG 50 @ $573.20 ($15 commission) -> Now you own 50 shares. The cost basis is 50 * 471.09 + 7.50 = $23,562.
       (Remember that commission costs are apportioned across all the shares you bought originally.)

       cost basis: cost basis = initial investment * (remaining quantity / initial quantity)
    */
    public BigDecimal costBasis() {
        return getInitialInvestment().multiply(getRemainingQuantity()
                .divide(getInitialQuantity(), RoundingMode.HALF_EVEN));
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

    public BigDecimal marketValue(BigDecimal sharePrice) {
        return getRemainingQuantity().multiply(sharePrice);
    }

    public BigDecimal returnsGain(BigDecimal sharePrice) {
        //returns gain = market_value + cash in - cash out.
        return marketValue(sharePrice).add(cashIn()).add(cashOut()); //cashOut is cash value of opening transaction: it is negative
    }

    public BigDecimal overallReturn(BigDecimal sharePrice) {
        //Overall return = returns gain / cash out
                                                        //Should we negate here or in the cashValue of opening transaction?
        return returnsGain(sharePrice).divide(cashOut().negate(), RoundingMode.HALF_EVEN);
    }

    public BigDecimal gain(BigDecimal sharePrice) {
        return marketValue(sharePrice).subtract(costBasis());
    }

    public BigDecimal todaysGain(BigDecimal priceChange) {
        return getRemainingQuantity().multiply(priceChange);
    }

    public BigDecimal gainPercentage(BigDecimal sharePrice) {
        return gain(sharePrice).divide(costBasis(), RoundingMode.HALF_EVEN);
    }

}
