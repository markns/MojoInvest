package com.mns.alphaposition.server.engine.portfolio;

import com.mns.alphaposition.server.engine.transaction.Transaction;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final Logger logger = LoggerFactory.getLogger(Portfolio.class);

    private BigDecimal cash;

    private BigDecimal initialInvestment;

    private HashMap<String, Position> positions;

    public Portfolio() {
        this.cash = BigDecimal.ZERO;
        this.initialInvestment = BigDecimal.ZERO;
        this.positions = new HashMap<String, Position>();
    }

    public Portfolio(BigDecimal initialInvestment) {
        this.cash = initialInvestment;
        this.initialInvestment = initialInvestment;
        this.positions = new HashMap<String, Position>();
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void depositCash(BigDecimal decimal) {
        cash = cash.add(decimal);
    }

    public void add(Transaction transaction) throws PositionException {
        String symbol = transaction.getSymbol();
        if (!positions.containsKey(symbol)) {
            positions.put(symbol, new Position(symbol));
        }
        positions.get(symbol).add(transaction);

//        logger.debug("Adding {} to {}",
//                transaction.getCashValue().setScale(2, RoundingMode.HALF_UP), this);
        cash = cash.add(transaction.getCashValue());
    }

    public Position get(String symbol) {
        return positions.get(symbol);
    }

    public HashMap<String, Position> getPositions() {
        return positions;
    }

    public HashMap<String, Position> getCurrentPositions() {
        HashMap<String, Position> currentPositions = new HashMap<String, Position>();
        for (Position position : positions.values()) {
            if (position.shares().compareTo(BigDecimal.ZERO) > 0) {
                currentPositions.put(position.getSymbol(), position);
            }
        }
        return currentPositions;
    }

    public String getHoldings() {
        StringBuilder sb = new StringBuilder();
        for (Position position : positions.values()) {
            sb.append(position.getSymbol())
                    .append(" - ")
                    .append(position.shares())
                    .append(" / ");
        }
        return sb.toString();
    }

    public void add(Position position) {
        positions.put(position.getSymbol(), position);
    }

    public int size() {
        return positions.size();
    }

    public BigDecimal costBasis() {
        BigDecimal costBasis = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            if (position.shares().compareTo(BigDecimal.ZERO) == 0)
                continue;
            costBasis = costBasis.add(position.costBasis());
        }
        return costBasis;
    }

    public BigDecimal marketValue(Map<String, BigDecimal> sharePrices) {
        BigDecimal marketValue = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            if (position.shares().compareTo(BigDecimal.ZERO) == 0)
                continue;
            BigDecimal sharePrice = sharePrices.get(position.getSymbol());
            marketValue = marketValue.add(position.marketValue(sharePrice));
        }
        return marketValue;
    }

    public BigDecimal gain(Map<String, BigDecimal> sharePrices) {
        BigDecimal gain = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            if (position.shares().compareTo(BigDecimal.ZERO) == 0)
                continue;
            BigDecimal sharePrice = sharePrices.get(position.getSymbol());
            gain = gain.add(position.gain(sharePrice));
        }
        return gain;
    }

    public BigDecimal gainPercentage(Map<String, BigDecimal> sharePrices) {
        BigDecimal gainPercentage = BigDecimal.ZERO;
        try {
            gainPercentage = gain(sharePrices).divide(costBasis(), MathContext.DECIMAL32)
                    //multiply by 100 for percentage
                    .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
        } catch (ArithmeticException e) {
            //TODO: Is this acceptable?
            //pass;
        }
        return gainPercentage;
    }

    public BigDecimal returnsGain(Map<String, BigDecimal> sharePrices) {
        BigDecimal returnsGain = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            BigDecimal sharePrice = sharePrices.get(position.getSymbol());
            returnsGain = returnsGain.add(position.returnsGain(sharePrice));
        }
        return returnsGain;
    }

    public BigDecimal cashOut() {
        BigDecimal cashOut = BigDecimal.ZERO;
        for (Position position : positions.values()) {
            cashOut = cashOut.add(position.cashOut());
        }
        return cashOut;
    }

    public BigDecimal overallReturn(Map<String, BigDecimal> sharePrices) {
        return returnsGain(sharePrices).divide(cashOut().negate(), MathContext.DECIMAL32)
                //multiply by 100 for percentage
                .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
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

    public void removeEmptyPositions() {
        List<String> positionsToRemove = new ArrayList<String>();
        for (Position position : positions.values()) {
            if (position.shares().equals(BigDecimal.ZERO)) {
                positionsToRemove.add(position.getSymbol());
            }
        }
        for (String symbol : positionsToRemove) {
            positions.remove(symbol);
        }

    }

    public BigDecimal getInitialInvestment() {
        return initialInvestment;
    }
}
