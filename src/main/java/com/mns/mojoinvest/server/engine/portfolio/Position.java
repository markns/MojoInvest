package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;


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

    private QuoteDao quoteDao;

    private Fund fund;

    private List<Lot> lots;

    public Position(QuoteDao quoteDao, Fund fund) {
        this.quoteDao = quoteDao;
        this.fund = fund;
        this.lots = new ArrayList<Lot>();
    }

    public Fund getFund() {
        return fund;
    }


    private Quote getQuote(LocalDate date) {
        //TODO: cache here?
        return quoteDao.get(fund, date);
    }

    public List<Lot> getLots() {
        return lots;
    }

    //TODO: Maintain list of original transactions for display in transactionsview. Sell transactions can be split across lots

    public void add(Transaction transaction) throws PortfolioException {
        if (transaction instanceof BuyTransaction) {
            add((BuyTransaction) transaction);
        } else if (transaction instanceof SellTransaction) {
            add((SellTransaction) transaction);
        } else {
            throw new PortfolioException("Unrecognised transaction type");
        }
    }

    public void add(BuyTransaction transaction) throws PortfolioException {
        if (!fund.equals(transaction.getFund())) {
            throw new PortfolioException("Attempt to add a " + transaction.getFund() +
                    " transaction to a " + fund + " position");
        }
        lots.add(new Lot(transaction));
    }

    public void add(SellTransaction transaction) throws PortfolioException {
        if (!fund.equals(transaction.getFund())) {
            throw new PortfolioException("Attempt to add a " + transaction.getFund() +
                    " transaction to a " + fund + " position");
        }
        if (!saleIsValid(transaction)) {
            throw new PortfolioException(this + " is not large enough to be able " +
                    "to meet sale " + transaction);
        }
        updateLots(transaction);
    }

    private boolean saleIsValid(Transaction transaction) {
        BigDecimal openPosition = BigDecimal.ZERO;
        for (Lot lot : lots) {
            openPosition = openPosition.add(lot.getRemainingQuantity());
        }
        return openPosition.compareTo(transaction.getQuantity()) != -1;
    }


    public void updateLots(SellTransaction transaction)
            throws PortfolioException {

        for (Lot lot : lots) {
            BigDecimal remainder = lot.getRemainingQuantity()
                    .subtract(transaction.getQuantity());
            if (remainder.compareTo(BigDecimal.ZERO) < 0) {

                //Commission is split between the virtual transactions
                BigDecimal commission = transaction.getCommission().divide(new BigDecimal("2"));

                SellTransaction closingTransaction = new SellTransaction(transaction.getFund(),
                        transaction.getDate(), lot.getRemainingQuantity(), transaction.getPrice(),
                        commission);

                lot.addClosingTransaction(closingTransaction);

                transaction = new SellTransaction(transaction.getFund(),
                        transaction.getDate(), remainder.negate(), transaction.getPrice(),
                        commission);

            } else {
                lot.addClosingTransaction(transaction);
            }
        }
    }

    public BigDecimal shares() {
        BigDecimal shares = BigDecimal.ZERO;
        for (Lot lot : lots) {
            shares = shares.add(lot.getRemainingQuantity());
        }
        return shares;
    }

    //TODO: Should we invert logic to match lot.closed()?
    public boolean open() {
        return shares().compareTo(BigDecimal.ZERO) > 0;
    }

//    overallReturn

    /*
    The lot calculations are by far the trickiest part of entire process. Once that step is done, the summary values
    for each security are calculated. These are the values that appear in each row under the Performance tab. First,
    cost basis, market value, gain, and todays gain are all computed as the sum of the corresponding values of all the
    lots for a security.
    */


    public BigDecimal costBasis() {
        BigDecimal costBasis = BigDecimal.ZERO;
        for (Lot lot : lots) {
            costBasis = costBasis.add(lot.costBasis());
        }
        return costBasis;
    }

    public BigDecimal cashOut() {
        BigDecimal cashOut = BigDecimal.ZERO;
        for (Lot lot : lots) {
            cashOut = cashOut.add(lot.cashOut());
        }
        return cashOut;
    }

    public BigDecimal cashIn() {
        BigDecimal cashIn = BigDecimal.ZERO;
        for (Lot lot : lots) {
            cashIn = cashIn.add(lot.cashIn());
        }
        return cashIn;
    }

    public BigDecimal marketValue(LocalDate date) {
        BigDecimal marketValue = BigDecimal.ZERO;
        for (Lot lot : lots) {
            if (!lot.getOpeningTransaction().getDate().isAfter(date)) {
                Quote quote = getQuote(date);
                marketValue = marketValue.add(lot.marketValue(date, quote.getClose()));
            }
        }
        return marketValue;
    }

    public BigDecimal gain(LocalDate date) {
        Quote quote = getQuote(date);
        BigDecimal gain = BigDecimal.ZERO;
        for (Lot lot : lots) {
            gain = gain.add(lot.gain(quote.getClose()));
        }
        return gain;
    }

    public BigDecimal todaysGain(BigDecimal priceChange) {
        BigDecimal todaysGain = BigDecimal.ZERO;
        for (Lot lot : lots) {
            todaysGain = todaysGain.add(lot.todaysGain(priceChange));
        }
        return todaysGain;
    }

    /*
     * Then the gain percentage is calculated by:
     *  gain percentage = gain / cost basis
     */
    public BigDecimal gainPercentage(LocalDate date) {
        return gain(date).divide(costBasis(), MathContext.DECIMAL32)
                //multiply by 100 for percentage
                .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
    }

    public BigDecimal returnsGain(LocalDate date) {
        Quote quote = getQuote(date);
        BigDecimal returnsGain = BigDecimal.ZERO;
        for (Lot lot : lots) {
            returnsGain = returnsGain.add(lot.returnsGain(quote.getClose()));
        }
        return returnsGain;
    }


    /*
     * The total return for each security is calculated similarly: Returns gain and cash out are summed over all the
     * lots for the security, then the total return is calculated by:
     *  total return = returns gain / cash out
     */
    public BigDecimal totalReturn(LocalDate date) {
        return returnsGain(date).divide(cashOut().negate(), MathContext.DECIMAL32)
                //multiply by 100 for percentage
                .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
    }
}
