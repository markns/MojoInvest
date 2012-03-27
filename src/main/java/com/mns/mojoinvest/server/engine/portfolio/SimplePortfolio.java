package com.mns.mojoinvest.server.engine.portfolio;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import com.mns.mojoinvest.shared.params.PortfolioParams;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.logging.Logger;

/**
 * A portfolio is a collection of positions that the user holds in various securities, plus metadata.
 * <p/>
 * A portfolio feed lists all of the user's portfolios. Each portfolio entry contains the portfolio's
 * title along with data such as currency and total market value. Each portfolio entry also contains
 * a link to the portfolio's position feed.
 * <p/>
 * Next, the summary values for the portfolio are calculated. These are the values that appear along
 * the final row in the Performance tab. First, for each of the securities in the portfolio, cost basis,
 * market value, gain, and today's gain are converted from the security's currency to the portfolio
 * currency (which you can set in the Edit Portfolio page). Then the converted values are summed over
 * all the currencies to give the portfolio values. Market value is adjusted by adding any cash
 * deposits and subtracting any cash withdrawals. Then gain percentage is computed in the by-now
 * familiar way:
 * <p/>
 * gain percentage = gain / cost basis
 * <p/>
 * Finally, the overall return is computed by converting the returns gain and cash out from each
 * of the securities from the security currency to the portfolio currency, then summing them to
 * getRanking portfolio values. The total return is the calculated by:
 * <p/>
 * total return = returns gain / cash out
 */
public class SimplePortfolio implements Portfolio {

    private static final Logger log = Logger.getLogger(SimplePortfolio.class.getName());

    private Map<String, Position> positions;
    private FundDao fundDao;
    private QuoteDao quoteDao;
    private NavigableMap<LocalDate, BigDecimal> cashFlows;
    private BigDecimal transactionCost;

    @Inject
    public SimplePortfolio(FundDao fundDao, QuoteDao quoteDao, @Assisted PortfolioParams params) {
        this.fundDao = fundDao;
        this.quoteDao = quoteDao;
        this.positions = new HashMap<String, Position>();
        this.cashFlows = new TreeMap<LocalDate, BigDecimal>();
        this.cashFlows.put(new LocalDate(params.getCreationDate()),
                BigDecimal.valueOf(params.getInitialInvestment()));
        this.transactionCost = BigDecimal.valueOf(params.getTransactionCost());
    }

    @Override
    public BigDecimal getCash(LocalDate date) {
        BigDecimal cash = BigDecimal.ZERO;
        for (BigDecimal cashFlow : cashFlows.headMap(date, true).values()) {
            cash = cash.add(cashFlow);
        }
        return cash;
    }

    private void addCashFlow(LocalDate date, BigDecimal amount) {
        if (!cashFlows.containsKey(date))
            cashFlows.put(date, BigDecimal.ZERO);
        cashFlows.put(date, cashFlows.get(date).add(amount));
    }

    public BigDecimal getTransactionCost() {
        return transactionCost;
    }

    @Override
    public boolean contains(String fund, LocalDate date) {
        return getOpenPositions(date).containsKey(fund);
    }

    @Override
    public Position getPosition(String fund) {
        return positions.get(fund);
    }

    @Override
    public void add(Transaction transaction) throws PortfolioException {
        if (transaction instanceof BuyTransaction) {
            add((BuyTransaction) transaction);
        } else {
            add((SellTransaction) transaction);
        }
    }

    @Override
    public void add(BuyTransaction transaction) throws PortfolioException {
        if (transaction.getInitialInvestment()
                .compareTo(getCash(transaction.getDate())) > 0)
            throw new PortfolioException("Not enough cash in portfolio to buy "
                    + transaction);

        if (!positions.containsKey(transaction.getFund())) {
            positions.put(transaction.getFund(), new Position(quoteDao, fundDao.get(transaction.getFund())));
        }
        addCashFlow(transaction.getDate(), transaction.getCashValue());
        positions.get(transaction.getFund()).add(transaction);
    }

    @Override
    public void add(SellTransaction transaction) throws PortfolioException {
        if (!positions.containsKey(transaction.getFund())) {
            throw new PortfolioException("Cannot sell a fund that is not held");
        }
        addCashFlow(transaction.getDate(), transaction.getCashValue());
        positions.get(transaction.getFund()).add(transaction);
    }

    public Collection<Position> getPositions() {
        return positions.values();
    }

    @Override
    public Map<String, Position> getOpenPositions(LocalDate date) {
        Map<String, Position> currentPositions = new HashMap<String, Position>();
        for (Position position : positions.values()) {
            if (position.open(date)) {
                currentPositions.put(position.getFund().getSymbol(), position);
            }
        }
        return currentPositions;
    }

    @Override
    public int openPositionCount(LocalDate date) {
        return getOpenPositions(date).size();
    }

    @Override
    public Collection<String> getFunds() {
        return positions.keySet();
    }

    @Override
    public Collection<String> getActiveFunds(LocalDate date) {
        return getOpenPositions(date).keySet();
    }

    /*
     Next, the summary values for the portfolio are calculated. These are the values that appear along the final row in
      the Performance tab. First, for each of the securities in the portfolio, cost basis, market value, gain, and
      today's gain are converted from the security's currency to the portfolio currency (which you can set in the Edit
      Portfolio page). Then the converted values are summed over all the currencies to give the portfolio values. Market
      value is adjusted by adding any cash deposits and subtracting any cash withdrawals.
     */

    @Override
    public BigDecimal costBasis(LocalDate date) {
        BigDecimal costBasis = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            //adjust for currency
            costBasis = costBasis.add(position.costBasis(date));
        }
        return costBasis;
    }

    @Override
    public BigDecimal cashOut(LocalDate date) {
        BigDecimal cashOut = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            //adjust for currency
            cashOut = cashOut.add(position.cashOut(date));
        }
        return cashOut;
    }

    @Override
    public BigDecimal marketValue(LocalDate date) {
        BigDecimal marketValue = BigDecimal.ZERO;
        for (Position position : positions.values()) {

            //adjust for currency
            marketValue = marketValue.add(position.marketValue(date));
        }
        return marketValue.add(getCash(date));
    }

    @Override
    public BigDecimal gain(LocalDate date) {
        BigDecimal gain = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            //adjust for currency
            System.out.println(position + " " + position.gain(date));
            gain = gain.add(position.gain(date));
        }
        return gain;
    }

    /*
        Then gain percentage is computed in the by-now familiar way:
        gain percentage = gain / cost basis
     */

    @Override
    public BigDecimal gainPercentage(LocalDate date) {
        System.out.println(gain(date) + " " + costBasis(date));
        BigDecimal costBasis = costBasis(date);
        if (costBasis.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;
        return gain(date).divide(costBasis, MathContext.DECIMAL32)
                //multiply by 100 for percentage
                .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
    }


    /**
     * returns gain = market_value + cash in - cash out
     *
     * @param date
     * @return
     */
    @Override
    public BigDecimal returnsGain(LocalDate date) {
        BigDecimal returnsGain = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            //adjust for currency
            returnsGain = returnsGain.add(position.returnsGain(date));
        }
        return returnsGain;
    }

    /**
     * Finally, the overall return is computed by converting the returns gain
     * and cash out from each of the securities from the security currency to
     * the portfolio currency, then summing them to get portfolio values.
     * The total return is calculated by:
     * overall return = returns gain / cash out
     *
     * @param date
     * @return
     */
    @Override
    public BigDecimal overallReturn(LocalDate date) {
        if (openPositionCount(date) == 0)
            return BigDecimal.ZERO;
        //negate cashOut in division to maintain direction of gain
        return returnsGain(date).divide(cashOut(date).negate(), MathContext.DECIMAL32)
                .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
    }


    public List<BigDecimal> marketValue(List<LocalDate> dates) {
        List<BigDecimal> portfolioValues = new ArrayList<BigDecimal>(Collections.nCopies(dates.size(), BigDecimal.ZERO));

        log.info("Calculating portfolio market values from " + positions.size() + " positions");
        for (Position position : positions.values()) {
            List<BigDecimal> positionValues = position.marketValue(dates);
            //Add values of the two lists
            for (int i = 0; i < portfolioValues.size(); i++) {
                portfolioValues.set(i, portfolioValues.get(i).add(positionValues.get(i)));
            }
        }

        log.info("Adding cash to portfolio values");
        for (int i = 0; i < dates.size(); i++) {
            portfolioValues.set(i, portfolioValues.get(i).add(getCash(dates.get(i))));
        }

        return portfolioValues;
    }


}
