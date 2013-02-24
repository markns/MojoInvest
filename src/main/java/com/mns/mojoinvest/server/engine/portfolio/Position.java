package com.mns.mojoinvest.server.engine.portfolio;

import com.google.common.collect.Iterables;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.DataAccessException;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


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

    private final QuoteDao quoteDao;
    private final Fund fund;
    private final LocalDate openingDate;
    private final List<Lot> lots;
    private final List<Transaction> transactions;

    private Map<LocalDate, BigDecimal> marketValueCache = new HashMap<LocalDate, BigDecimal>();

    private static final Logger log = Logger.getLogger(Position.class.getName());

    public Position(Fund fund, LocalDate openingDate, QuoteDao quoteDao) {
        this.quoteDao = quoteDao;
        this.fund = fund;
        this.lots = new ArrayList<Lot>();
        this.transactions = new ArrayList<Transaction>();
        this.openingDate = openingDate;
    }

    public Fund getFund() {
        return fund;
    }

    public List<Lot> getLots() {
        return lots;
    }

    public void add(Transaction transaction) throws PortfolioException {
        checkOpeningDate(transaction);
        if (transaction instanceof BuyTransaction) {
            add((BuyTransaction) transaction);
        } else if (transaction instanceof SellTransaction) {
            add((SellTransaction) transaction);
        } else {
            throw new PortfolioException("Unrecognised transaction type");
        }
    }

    public void add(BuyTransaction transaction) throws PortfolioException {
        checkOpeningDate(transaction);
        if (!fund.getSymbol().equals(transaction.getFund())) {
            throw new PortfolioException("Attempt to add a " + transaction.getFund() +
                    " transaction to a " + fund + " position");
        }
        transactions.add(transaction);
        lots.add(new Lot(quoteDao, transaction));
    }

    public void add(SellTransaction transaction) throws PortfolioException {
        checkOpeningDate(transaction);
        //TODO: Check if a later transaction has already been added
        if (!fund.getSymbol().equals(transaction.getFund())) {
            throw new PortfolioException("Attempt to add a " + transaction.getFund() +
                    " transaction to a " + fund + " position");
        }
        if (!saleIsValid(transaction)) {
            throw new PortfolioException(this + " is not large enough to be able " +
                    "to meet sale " + transaction);
        }
        transactions.add(transaction);
        updateLots(transaction);
    }

    private void checkOpeningDate(Transaction transaction) {
        if (transaction.getDate().isBefore(openingDate))
            throw new IllegalStateException("Attempt to add a transaction to a position " +
                    "before the position's creation date");
    }

    private boolean saleIsValid(Transaction transaction) {
        BigDecimal openPosition = BigDecimal.ZERO;
        for (Lot lot : lots) {
            openPosition = openPosition.add(lot.getRemainingQuantity(transaction.getDate()));
        }
        return openPosition.compareTo(transaction.getQuantity()) != -1;
    }

    public void updateLots(SellTransaction transaction)
            throws PortfolioException {

        for (Lot lot : lots) {
            if (lot.getCloseDate() != null) {
                continue;
            }
            BigDecimal remainingQuantity = lot.getRemainingQuantity(transaction.getDate());
            BigDecimal difference = remainingQuantity.subtract(transaction.getQuantity());
            if (difference.compareTo(BigDecimal.ZERO) < 0) {

                //Commission is split between the virtual transactions
                BigDecimal commission = transaction.getCommission().divide(new BigDecimal("2"));

                SellTransaction closingTransaction = new SellTransaction(transaction.getFund(),
                        transaction.getDate(), remainingQuantity,
                        transaction.getPrice(), commission);

                lot.addSellTransaction(closingTransaction);

                transaction = new SellTransaction(transaction.getFund(),
                        transaction.getDate(), difference.negate(), transaction.getPrice(),
                        commission);

            } else {
                lot.addSellTransaction(transaction);
                break;
            }
        }
    }

    public BigDecimal shares(LocalDate date) {
        if (date.isBefore(openingDate))
            return BigDecimal.ZERO;

        BigDecimal shares = BigDecimal.ZERO;
        for (Lot lot : lots) {
            if (!lot.openedAfter(date)) {
                shares = shares.add(lot.getRemainingQuantity(date));
            }
        }
        return shares;
    }

    public boolean open(LocalDate date) {
        return !date.isBefore(openingDate) &&
                shares(date).compareTo(BigDecimal.ZERO) > 0;
    }

    /*
    The lot calculations are by far the trickiest part of entire process. Once that step is done, the summary values
    for each security are calculated. These are the values that appear in each row under the Performance tab. First,
    cost basis, market value, gain, and todays gain are all computed as the sum of the corresponding values of all the
    lots for a security.
    */

    public BigDecimal costBasis(LocalDate date) {
        if (date.isBefore(openingDate))
            return BigDecimal.ZERO;

        BigDecimal costBasis = BigDecimal.ZERO;
        for (Lot lot : lots) {
            if (!lot.openedAfter(date)) {
                costBasis = costBasis.add(lot.costBasis(date));
            }
        }
        return costBasis;
    }

    public BigDecimal cashOut(LocalDate date) {
        if (date.isBefore(openingDate))
            return BigDecimal.ZERO;

        BigDecimal cashOut = BigDecimal.ZERO;
        for (Lot lot : lots) {
            if (!lot.openedAfter(date)) {
                cashOut = cashOut.add(lot.cashOut());
            }
        }
        return cashOut;
    }

    public BigDecimal cashIn(LocalDate date) {
        if (date.isBefore(openingDate))
            return BigDecimal.ZERO;

        BigDecimal cashIn = BigDecimal.ZERO;
        for (Lot lot : lots) {
            cashIn = cashIn.add(lot.cashIn(date));
        }
        return cashIn;
    }

    public BigDecimal marketValue(LocalDate date) throws PortfolioException {
        if (date.isBefore(openingDate))
            return BigDecimal.ZERO;

        if (marketValueCache.containsKey(date))
            return marketValueCache.get(date);
        //log.fine(date + " Calculating market value for " + this);

        BigDecimal marketValue = BigDecimal.ZERO;
        for (Lot lot : lots) {
            if (!lot.openedAfter(date) && !lot.closed(date)) {
                BigDecimal close = getNav(date);
                marketValue = marketValue.add(lot.marketValue(date, close));
            }
        }
//        if (BigDecimal.ZERO.compareTo(marketValue) != 0)
//            log.fine(date + " Calculated market value for " + this + " as " + marketValue);
        marketValueCache.put(date, marketValue);
        return marketValue;
    }

    public BigDecimal gain(LocalDate date) throws PortfolioException {
        if (date.isBefore(openingDate))
            return BigDecimal.ZERO;

        BigDecimal gain = BigDecimal.ZERO;
        for (Lot lot : lots) {
            if (!lot.openedAfter(date)) {
                gain = gain.add(lot.gain(date, getNav(date)));
            }
        }
        return gain;
    }

    /*
     * Then the gain percentage is calculated by:
     *  gain percentage = gain / cost basis
     */
    public BigDecimal gainPercentage(LocalDate date) throws PortfolioException {
        if (date.isBefore(openingDate))
            return BigDecimal.ZERO;

        return gain(date).divide(costBasis(date), MathContext.DECIMAL32)
                //multiply by 100 for percentage
                .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
    }

    public BigDecimal returnsGain(LocalDate date) throws PortfolioException {
        if (date.isBefore(openingDate))
            return BigDecimal.ZERO;

        BigDecimal returnsGain = BigDecimal.ZERO;
        for (Lot lot : lots) {
            if (!lot.openedAfter(date)) {
                returnsGain = returnsGain.add(lot.returnsGain(date, getNav(date)));
            }
        }
        return returnsGain;
    }


    /*
     * The total return for each security is calculated similarly: Returns gain and cash out are summed over all the
     * lots for the security, then the total return is calculated by:
     *  total return = returns gain / cash out
     */
    public BigDecimal totalReturn(LocalDate date) throws PortfolioException {
        if (date.isBefore(openingDate))
            return BigDecimal.ZERO;

        return returnsGain(date).divide(cashOut(date).negate(), MathContext.DECIMAL32)
                //multiply by 100 for percentage
                .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
    }

    private BigDecimal getNav(LocalDate date) throws PortfolioException {
        Quote quote;
        try {
            quote = quoteDao.get(fund, date);
        } catch (DataAccessException e) {
            throw new PortfolioException("Unable to get close price for " + this + " on " + date);
        }
//        log.fine("Loaded quote " + quote);
        return quote.getTrNav();
    }

    public boolean canSellOn(LocalDate rebalanceDate, int minHoldingPeriod) {
        LocalDate lastTransactionDate = Iterables.getLast(transactions).getDate();
        if (lastTransactionDate.plusWeeks(minHoldingPeriod).isAfter(rebalanceDate))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Position{" +
                "fund=" + fund.getSymbol() +
                ", lots=" + lots.size() +
                '}';
    }
}
