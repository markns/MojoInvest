package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.DataAccessException;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.logging.Logger;

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

    private static final Logger log = Logger.getLogger(Lot.class.getName());

    private final LocalDate openDate;
    private LocalDate closeDate;

    private final BuyTransaction openingTransaction;
    private Map<LocalDate, BigDecimal> marketValueCache = new HashMap<LocalDate, BigDecimal>();

    private final NavigableMap<LocalDate, SellTransaction> sellTransactionsMap;
    private QuoteDao quoteDao;

    public Lot(QuoteDao quoteDao, BuyTransaction openingTransaction) {
        log.fine(openingTransaction.getDate() + " Creating new lot from " + openingTransaction);
        this.quoteDao = quoteDao;
        this.openingTransaction = openingTransaction;
        this.openDate = openingTransaction.getDate();
        this.sellTransactionsMap = new TreeMap<LocalDate, SellTransaction>();
    }

    public BuyTransaction getOpeningTransaction() {
        return openingTransaction;
    }

    public Collection<SellTransaction> getSellTransactions() {
        return sellTransactionsMap.values();
    }

    public Collection<SellTransaction> getSellTransactions(LocalDate date) {
        List<SellTransaction> transactions = new ArrayList<SellTransaction>();
        for (SellTransaction transaction : sellTransactionsMap.headMap(date, true).values()) {
            transactions.add(transaction);
        }
        return transactions;
    }

    public void addSellTransaction(SellTransaction transaction)
            throws PortfolioException {
        if (!saleIsValid(transaction))
            throw new PortfolioException("Lot is not large enough to be able to meet sale " + transaction);
        if (closesLot(transaction))
            closeDate = transaction.getDate();
        sellTransactionsMap.put(transaction.getDate(), transaction);
    }

    private boolean saleIsValid(Transaction transaction) {
        //TODO: throw exception if any transactions exist after transaction.getDate
        return getRemainingQuantity(transaction.getDate()).compareTo(transaction.getQuantity()) >= 0;
    }

    private boolean closesLot(Transaction transaction) {
        return getRemainingQuantity(transaction.getDate()).compareTo(transaction.getQuantity()) == 0;
    }

    /**
     * This is the share count of the transaction that opened the lot.
     *
     * @return initial quantity
     */
    public BigDecimal getInitialQuantity() {
        return openingTransaction.getQuantity();
    }

    /**
     * This is the number of shares that have not been matched with subsequent sell transactions.
     *
     * @param date
     * @return remaining quantity
     */
    public BigDecimal getRemainingQuantity(LocalDate date) {
        BigDecimal closingQuantity = BigDecimal.ZERO;
        for (SellTransaction closingTransaction : getSellTransactions(date)) {
            closingQuantity = closingQuantity.add(closingTransaction.getQuantity());
        }
        return getInitialQuantity().subtract(closingQuantity);
    }

    /**
     * This is the negative of the cash value of the transaction that opened the lot,
     * because the cash value is the effect on your bank account, but the initial investment is the opposite:
     * it is the value that has been "put into" the stock. So, for example, the cash value of a purchase of
     * 10 shares of GOOG at $350 is -$3500, and the initial investment is $3500.
     *
     * @return initial investment
     */
    public BigDecimal getInitialInvestment() {
        return openingTransaction.getInitialInvestment();
    }

    protected LocalDate getCloseDate() {
        return closeDate;
    }

    protected boolean openedAfter(LocalDate date) {
        return getOpeningTransaction().getDate().isAfter(date);
    }

    protected boolean closed(LocalDate date) {
//        return getRemainingQuantity(date).compareTo(BigDecimal.ZERO) == 0;
        return closeDate != null && !date.isBefore(closeDate);
    }

    /**
     * We should linger on Cost basis for a moment. When Google Finance presents summary statistics for your portfolio,
     * it computes those numbers based on the number of shares you still own. So, if you bought 100 shares of GOOG,
     * then sold them all, your cost basis will be reported as 0, as will your market value, gain, etc. If you only
     * sold 25 shares, then all of your statistics will be based on the 75 shares that you still own. Notice that since
     * this ratio is applied to your entire initial investment, which includes commission costs, only the portion of
     * your commission costs that applied to the stocks you still own will be considered.
     * <p/>
     * Some examples:
     * Suppose you buy 100 shares of GOOG on April 1, 2008:
     * Transaction: 4/1/2008 BUY GOOG 100 @ $471.09 ($15 commission) -> the cost basis is 100 * 471.09 + 15 = $47.124.00
     * Now Suppose you buy 100 shares of GOOG on April 1, 2008, but sell 50 on 5/5/2008:
     * Transaction: 4/1/2008 BUY GOOG 100 @ $471.09 ($15 commission) -> At this point you own 100 shares.
     * Transaction: 5/5/2008 SELL GOOG 50 @ $573.20 ($15 commission) -> Now you own 50 shares. The cost basis is 50 * 471.09 + 7.50 = $23,562.
     * (Remember that commission costs are apportioned across all the shares you bought originally.)
     * <p/>
     * cost basis: cost basis = initial investment * (remaining quantity / initial quantity)
     *
     * @param date
     * @return Cost Basis
     */
    public BigDecimal costBasis(LocalDate date) {
        return getInitialInvestment().multiply(getRemainingQuantity(date)
                .divide(getInitialQuantity(), MathContext.DECIMAL32));
    }

    /**
     * Cash value of the opening transaction
     *
     * @return cashOut
     */
    public BigDecimal cashOut() {
        return openingTransaction.getCashValue();
    }

    /**
     * Cash value of all the closing transactions
     *
     * @param date
     * @return cashIn
     */
    public BigDecimal cashIn(LocalDate date) {
        BigDecimal cashIn = BigDecimal.ZERO;
        for (Transaction transaction : getSellTransactions(date)) {
            cashIn = cashIn.add(transaction.getCashValue());
        }
        return cashIn;
    }

    /**
     * Calculate the market value of the lot including transactions up until the date
     *
     * @param date       date for which to calculate market value
     * @param sharePrice share price
     * @return marketValue
     */
    public BigDecimal marketValue(LocalDate date, BigDecimal sharePrice) {
        if (marketValueCache.containsKey(date))
            return marketValueCache.get(date);
        BigDecimal marketValue = getRemainingQuantity(date).multiply(sharePrice);
        log.fine(date + " Calculated market value for " + this + " as " + marketValue);
        marketValueCache.put(date, marketValue);
        return marketValue;
    }

    /**
     * gain: gain = market value - cost basis [Note that this might be negative!]
     *
     * @param date
     * @param sharePrice share price
     * @return change in market value for the remaining shares
     */
    public BigDecimal gain(LocalDate date, BigDecimal sharePrice) {
        return marketValue(date, sharePrice).subtract(costBasis(date));
    }

    /**
     * todays's gain: today's gain = remaining quantity * price change
     *
     * @param date
     * @param priceChange today's change in the share price
     * @return today's value gain
     */
    public BigDecimal todaysGain(LocalDate date, BigDecimal priceChange) {
        return getRemainingQuantity(date).multiply(priceChange);
    }

    /**
     * gain percentage: gain percentage = gain / cost basis
     *
     * @param date
     * @param sharePrice share price
     * @return gain expressed as percentage
     */
    public BigDecimal gainPercentage(LocalDate date, BigDecimal sharePrice) {
        BigDecimal gain = gain(date, sharePrice);
        if (gain.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return gain(date, sharePrice).divide(costBasis(date), MathContext.DECIMAL32);
    }

    /**
     * Similar to the gain computed earlier, except that it takes into account
     * the money you made on all transactions.
     * <p/>
     * returns gain = market_value + cash in - cash out.
     *
     * @param date
     * @param sharePrice share price
     * @return change in market value considering all shares in the lot
     */
    public BigDecimal returnsGain(LocalDate date, BigDecimal sharePrice) {
        return marketValue(date, sharePrice).add(cashIn(date)).add(cashOut()); //cashOut is cash value of opening transaction: it is negative
    }

    /**
     * The overall return rate is just the returns gain divided by the amount you paid
     * to establish the lot
     *
     * @param date
     * @param sharePrice share price
     * @return
     */
    public BigDecimal overallReturn(LocalDate date, BigDecimal sharePrice) {
        //Overall return = returns gain / cash out
        //Should we negate here or in the cashValue of opening transaction?
        return returnsGain(date, sharePrice).divide(cashOut().negate(), MathContext.DECIMAL32);
    }


    public List<BigDecimal> marketValue(List<LocalDate> dates) throws PortfolioException {
        List<BigDecimal> quantities = getRemainingQuantity(dates);
        List<BigDecimal> lotValues = new ArrayList<BigDecimal>(dates.size());
        for (int i = 0; i < dates.size(); i++) {
            if (quantities.get(i).equals(BigDecimal.ZERO)) {
                lotValues.add(BigDecimal.ZERO);
            } else {
                Quote quote = null;
                try {
                    quote = quoteDao.get(openingTransaction.getFund(), dates.get(i));
                } catch (DataAccessException e) {
                    throw new PortfolioException("Unable to calculate market value of lot", e);
                }
                lotValues.add(quantities.get(i).multiply(quote.getAdjClose()));
            }
        }
        return lotValues;
    }

    public List<BigDecimal> getRemainingQuantity(List<LocalDate> dates) {
        SortedMap<LocalDate, BigDecimal> quantities = new TreeMap<LocalDate, BigDecimal>();
        for (LocalDate date : dates) {
            if (!date.isBefore(openDate)) {
                quantities.put(date, getInitialQuantity());
            } else {
                quantities.put(date, BigDecimal.ZERO);
            }
        }
        for (SellTransaction sellTransaction : sellTransactionsMap.values()) {
            for (Map.Entry<LocalDate, BigDecimal> e : quantities.entrySet()) {
                if (!sellTransaction.getDate().isAfter(e.getKey())) {
                    quantities.put(e.getKey(),
                            e.getValue().subtract(sellTransaction.getQuantity()));
                }
            }
        }
        return new ArrayList<BigDecimal>(quantities.values());
    }


    @Override
    public String toString() {
        return "Lot{" +
                "symbol=" + openingTransaction.getFund() +
                ", openDate=" + openDate +
                '}';
    }
}
