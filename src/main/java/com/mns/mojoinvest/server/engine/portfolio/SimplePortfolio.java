package com.mns.mojoinvest.server.engine.portfolio;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.shared.params.PortfolioParams;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

    private HashMap<Fund, Position> positions;

    private QuoteDao quoteDao;

    private BigDecimal cash;

    private BigDecimal transactionCost;

    @Inject
    public SimplePortfolio(QuoteDao quoteDao, @Assisted PortfolioParams params) {
        this.quoteDao = quoteDao;
        this.positions = new HashMap<Fund, Position>();
        this.cash = BigDecimal.valueOf(params.getInitialInvestment());
        this.transactionCost = BigDecimal.valueOf(params.getTransactionCost());
    }

    @Override
    public BigDecimal getCash() {
        return cash;
    }

    public BigDecimal getTransactionCost() {
        return transactionCost;
    }

    @Override
    public boolean contains(Fund fund, LocalDate date) {
        return getActivePositions(date).containsKey(fund);
    }

    @Override
    public Position get(Fund fund) {
        return positions.get(fund);
    }

    @Override
    public void add(BuyTransaction transaction) throws PortfolioException {
        Fund fund = transaction.getFund();
        if (!positions.containsKey(fund)) {
            positions.put(fund, new Position(quoteDao, fund));
        }
        positions.get(fund).add(transaction);
        cash = cash.add(transaction.getCashValue());
    }

    @Override
    public void add(SellTransaction transaction) throws PortfolioException {
        Fund fund = transaction.getFund();
        if (!positions.containsKey(fund)) {
            throw new PortfolioException("Cannot sell a fund that is not held");
        }
        positions.get(fund).add(transaction);
        cash = cash.add(transaction.getCashValue());
    }

    //TODO: refactor to use position.open
    @Override
    public int numberOfActivePositions(LocalDate date) {
        int numberOfActivePostions = 0;
        for (Position position : positions.values()) {
            if (position.shares(date).compareTo(BigDecimal.ZERO) > 0) {
                numberOfActivePostions++;
            }
        }
        return numberOfActivePostions;
    }

    public Collection<Position> getPositions() {
        return positions.values();
    }

    //TODO: refactor to use position.open
    @Override
    public HashMap<Fund, Position> getActivePositions(LocalDate date) {
        HashMap<Fund, Position> currentPositions = new HashMap<Fund, Position>();
        for (Position position : positions.values()) {
            if (position.shares(date).compareTo(BigDecimal.ZERO) > 0) {
                currentPositions.put(position.getFund(), position);
            }
        }
        return currentPositions;
    }

    //TODO: refactor to use position.open
    @Override
    public Set<Fund> getActiveHoldings(LocalDate date) {
        Set<Fund> activeHoldings = new HashSet<Fund>();
        for (Position position : positions.values()) {
            if (position.shares(date).compareTo(BigDecimal.ZERO) > 0) {
                activeHoldings.add(position.getFund());
            }
        }
        return activeHoldings;
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
    public BigDecimal marketValue(LocalDate date) {
        BigDecimal marketValue = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            //adjust for currency
            marketValue = marketValue.add(position.marketValue(date));
        }
        return marketValue.add(cash);
    }

    @Override
    public BigDecimal gain(LocalDate date) {
        BigDecimal gain = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            //adjust for currency
            gain = gain.add(position.gain(date));
        }
        return gain;
    }

    @Override
    public BigDecimal todaysGain(LocalDate date, BigDecimal priceChange) {
        BigDecimal todaysGain = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            //adjust for currency
            todaysGain = todaysGain.add(position.todaysGain(date, priceChange));
        }
        return todaysGain;
    }

    /*
        Then gain percentage is computed in the by-now familiar way:
        gain percentage = gain / cost basis
     */

    @Override
    public BigDecimal gainPercentage(LocalDate date) {
        return gain(date).divide(costBasis(date), MathContext.DECIMAL32);
    }

    /*
        Finally, the overall return is computed by converting the returns gain and cash out from each of the securities
        from the security currency to the portfolio currency, then summing them to getRanking portfolio values. The total
        return is the calculated by:
        overall return = returns gain / cash out
     */

    @Override
    public BigDecimal overallReturn(LocalDate date) {
        if (positions.size() == 0)
            return BigDecimal.ZERO;

        return returnsGain(date).divide(cashOut(date), MathContext.DECIMAL32)
                .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
    }

    @Override
    public BigDecimal returnsGain(LocalDate date) {
        BigDecimal returnsGain = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            //adjust for currency
            returnsGain = returnsGain.add(position.returnsGain(date));
        }
        return returnsGain;
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


}
