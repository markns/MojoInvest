package com.mns.alphaposition.server.engine.portfolio;

import com.mns.alphaposition.server.engine.model.QuoteDao;
import com.mns.alphaposition.server.engine.transaction.BuyTransaction;
import com.mns.alphaposition.server.engine.transaction.SellTransaction;
import com.mns.alphaposition.shared.engine.model.Fund;
import com.mns.alphaposition.shared.params.PortfolioParams;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * get portfolio values. The total return is the calculated by:
 * <p/>
 * total return = returns gain / cash out
 */
public class Portfolio {

    private BigDecimal cash;

    private QuoteDao quoteDao;

    private HashMap<Fund, Position> positions;

    public Portfolio(PortfolioParams params, QuoteDao quoteDao) {
        this.cash = params.getInitialInvestment();
        this.positions = new HashMap<Fund, Position>();
        this.quoteDao = quoteDao;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public boolean contains(Fund fund) {
        return getActivePositions().containsKey(fund);
    }

    public Position get(Fund fund) {
        return positions.get(fund);
    }

    public void add(BuyTransaction transaction) throws PositionException {
        Fund fund = transaction.getFund();
        if (!positions.containsKey(fund)) {
            positions.put(fund, new Position(quoteDao, fund));
        }
        positions.get(fund).add(transaction);
        cash = cash.add(transaction.getCashValue());
    }

    public void add(SellTransaction transaction) throws PositionException {
        Fund fund = transaction.getFund();
        if (!positions.containsKey(fund)) {
            throw new PositionException("Cannot sell a fund that is not held");
        }
        positions.get(fund).add(transaction);
        cash = cash.add(transaction.getCashValue());
    }

    public int numberOfActivePositions() {
        int numberOfActivePostions = 0;
        for (Position position : positions.values()) {
            if (position.shares().compareTo(BigDecimal.ZERO) > 0) {
                numberOfActivePostions++;
            }
        }
        return numberOfActivePostions;
    }

    public HashMap<Fund, Position> getActivePositions() {
        HashMap<Fund, Position> currentPositions = new HashMap<Fund, Position>();
        for (Position position : positions.values()) {
            if (position.shares().compareTo(BigDecimal.ZERO) > 0) {
                currentPositions.put(position.getFund(), position);
            }
        }
        return currentPositions;
    }

    public Set<Fund> getActiveHoldings() {
        Set<Fund> activeHoldings = new HashSet<Fund>();
        for (Position position : positions.values()) {
            if (position.shares().compareTo(BigDecimal.ZERO) > 0) {
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

    public BigDecimal costBasis() {
        BigDecimal costBasis = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            //adjust for currency
            costBasis = costBasis.add(position.costBasis());
        }
        return costBasis;
    }

    public BigDecimal marketValue(LocalDate date) {
        BigDecimal marketValue = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            //adjust for currency
            marketValue = marketValue.add(position.marketValue(date));
        }
        return marketValue.add(cash);
    }

    public BigDecimal gain(LocalDate date) {
        BigDecimal gain = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            //adjust for currency
            gain = gain.add(position.gain(date));
        }
        return gain;
    }

    public BigDecimal todaysGain(BigDecimal priceChange) {
        BigDecimal todaysGain = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            //adjust for currency
            todaysGain = todaysGain.add(position.todaysGain(priceChange));
        }
        return todaysGain;
    }

    /*
        Then gain percentage is computed in the by-now familiar way:
        gain percentage = gain / cost basis
     */

    public BigDecimal gainPercentage(LocalDate date) {
        return gain(date).divide(costBasis(), RoundingMode.HALF_EVEN);
    }

    /*
        Finally, the overall return is computed by converting the returns gain and cash out from each of the securities
        from the security currency to the portfolio currency, then summing them to get portfolio values. The total
        return is the calculated by:
        overall return = returns gain / cash out
     */

    public BigDecimal overallReturn(LocalDate date) {
        return returnsGain(date).divide(cashOut(), RoundingMode.HALF_EVEN)
                .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
    }

    public BigDecimal returnsGain(LocalDate date) {
        BigDecimal returnsGain = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            //adjust for currency
            returnsGain = returnsGain.add(position.returnsGain(date));
        }
        return returnsGain;
    }

    public BigDecimal cashOut() {
        BigDecimal cashOut = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            //adjust for currency
            cashOut = cashOut.add(position.cashOut());
        }
        return cashOut;
    }


}
