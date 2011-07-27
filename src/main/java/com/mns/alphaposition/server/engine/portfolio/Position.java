package com.mns.alphaposition.server.engine.portfolio;

import com.mns.alphaposition.server.engine.transaction.BuyTransaction;
import com.mns.alphaposition.server.engine.transaction.SellTransaction;
import com.mns.alphaposition.server.engine.transaction.Transaction;
import com.mns.alphaposition.shared.engine.model.Fund;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A position is a collection of information about a security that the user holds.
 * <p/>
 * A position feed lists all of the positions in a particular portfolio. Each position entry
 * contains the ticker exchange and symbol for a stock, mutual fund, or other security, along
 * with the number of units of that security that the user holds. Each position entry also
 * contains a link to the position's transaction feed.
 * <p/>
 * You can't directly create, update, or delete position entries; positions are derived from
 * transactions. For example, a GOOG position is derived from the buy and sell transactions
 * for GOOG. Google Finance updates the position every time it receives a transaction for that
 * position.
 * <p/>
 * Therefore, to create or modify a position, send appropriate transactions on that position.
 * To delete a position, delete all of its transactions.
 * <p/>
 * Selling all of the shares in a position does not delete the position; it just sets the
 * position's value to zero.
 */
public class Position {

    private Fund fund;

    private Map<String, Transaction> transactions;

    private List<Lot> lots;

    public Position(Fund fund) {
        this.fund = fund;
        this.transactions = new HashMap<String, Transaction>();
        this.lots = new ArrayList<Lot>();
    }

    public Fund getFund() {
        return fund;
    }

    public Map<String, Transaction> getTransactions() {
        return transactions;
    }

    public List<Lot> getLots() {
        return lots;
    }

    public void add(Transaction transaction) throws PositionException {
        if (!fund.equals(transaction.getFund())) {
            throw new PositionException("Attempt to add a " + transaction.getFund() +
                    " transaction to a " + fund + " position");
        }
        if (transactions.containsKey(transaction.getRef())) {
            throw new PositionException("Attempt to add " + transaction + " which has " +
                    "already contributed to the position");
        }

        if (transaction instanceof BuyTransaction) {
            lots.add(new Lot((BuyTransaction) transaction));
        } else if (transaction instanceof SellTransaction) {
            if (!saleIsValid(transaction)) {
                throw new PositionException(this + " is not large enough to be able " +
                        "to meet sale " + transaction);
            }
            updateLots(transaction);
        }
        transactions.put(transaction.getRef(), transaction);
    }

    private boolean saleIsValid(Transaction transaction) {
        BigDecimal openPosition = BigDecimal.ZERO;
        for (Lot lot : lots) {
            openPosition = openPosition.add(lot.getRemainingQuantity());
        }
        return openPosition.compareTo(transaction.getUnits()) != -1;
    }

    private boolean updateLots(Transaction tx) {

        for (Lot lot : lots) {
            if (!lot.closed()) {

                BigDecimal remainder = lot.getRemainingQuantity().subtract(tx.getUnits());

                if (remainder.compareTo(BigDecimal.ZERO) == -1) {

                    //TODO: What do we do with the commission - should it be split?
                    Transaction closingTransaction = new SellTransaction(tx.getFund(),
                            tx.getDate(), lot.getRemainingQuantity(), tx.getPrice(),
                            tx.getCommission());

                    Transaction overflowTransaction = new SellTransaction(tx.getFund(),
                            tx.getDate(), remainder.negate(), tx.getPrice(),
                            tx.getCommission());

                    lot.addClosingTransaction(closingTransaction);
                    lot.setRemainingQuantity(BigDecimal.ZERO);

                    if (updateLots(overflowTransaction))
                        break;

                } else {
                    lot.addClosingTransaction(tx);
                    lot.setRemainingQuantity(remainder);

                    return true;
                }
            }
        }
        return true;
    }

    public BigDecimal shares() {
        BigDecimal shares = BigDecimal.ZERO;
        for (Lot lot : lots) {
            shares = shares.add(lot.getRemainingQuantity());
        }
        return shares;
    }

    public BigDecimal costBasis() {
        BigDecimal costBasis = BigDecimal.ZERO;
        for (Lot lot : lots) {
            costBasis = costBasis.add(lot.costBasis());
        }
        return costBasis;
    }

    public BigDecimal marketValue(BigDecimal sharePrice) {
        BigDecimal marketValue = BigDecimal.ZERO;
        for (Lot lot : lots) {
            marketValue = marketValue.add(lot.marketValue(sharePrice));
        }
        return marketValue;
    }

    public BigDecimal gain(BigDecimal sharePrice) {
        BigDecimal gain = BigDecimal.ZERO;
        for (Lot lot : lots) {
            gain = gain.add(lot.gain(sharePrice));
        }
        return gain;
    }

    public BigDecimal gainPercentage(BigDecimal sharePrice) {
        return gain(sharePrice).divide(costBasis(), MathContext.DECIMAL32)
                //multiply by 100 for percentage
                .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
    }

    public BigDecimal todaysGain(BigDecimal priceChange) {
        BigDecimal todaysGain = BigDecimal.ZERO;
        for (Lot lot : lots) {
            todaysGain = todaysGain.add(lot.todaysGain(priceChange));
        }
        return todaysGain;
    }

    public BigDecimal overallReturn(BigDecimal sharePrice) {
        return returnsGain(sharePrice).divide(cashOut().negate(), MathContext.DECIMAL32)
                //multiply by 100 for percentage
                .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
    }

    public BigDecimal returnsGain(BigDecimal sharePrice) {
        BigDecimal returnsGain = BigDecimal.ZERO;
        for (Lot lot : lots) {
            returnsGain = returnsGain.add(lot.returnsGain(sharePrice));
        }
        return returnsGain;
    }

    public BigDecimal cashIn() {
        BigDecimal cashIn = BigDecimal.ZERO;
        for (Lot lot : lots) {
            cashIn = cashIn.add(lot.cashIn());
        }
        return cashIn;
    }

    //TODO: Consider making cashout an absolute value
    public BigDecimal cashOut() {
        BigDecimal cashOut = BigDecimal.ZERO;
        for (Lot lot : lots) {
            cashOut = cashOut.add(lot.cashOut());
        }
        return cashOut;
    }
}
