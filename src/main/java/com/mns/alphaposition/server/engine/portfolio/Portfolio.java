package com.mns.alphaposition.server.engine.portfolio;

import com.mns.alphaposition.server.engine.transaction.Transaction;
import com.mns.alphaposition.shared.engine.model.Fund;
import com.mns.alphaposition.shared.params.PortfolioParams;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

    private BigDecimal initialInvestment;

    private HashMap<Fund, Position> positions;

    public Portfolio(PortfolioParams params) {
        this.cash = params.getInitialInvestment();
        this.initialInvestment = params.getInitialInvestment();
        this.positions = new HashMap<Fund, Position>();
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

    public void add(Transaction transaction) throws PositionException {
        Fund fund = transaction.getFund();
        if (!positions.containsKey(fund)) {
            positions.put(fund, new Position(fund));
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


    //    public BigDecimal costBasis() {
//        BigDecimal costBasis = BigDecimal.ZERO;
//        for (Position position : positions.values()) {
//            if (position.shares().compareTo(BigDecimal.ZERO) == 0)
//                continue;
//            costBasis = costBasis.add(position.costBasis());
//        }
//        return costBasis;
//    }
//
//    public BigDecimal gain(Map<Fund, BigDecimal> sharePrices) {
//        BigDecimal gain = BigDecimal.ZERO;
//        for (Position position : positions.values()) {
//            if (position.shares().compareTo(BigDecimal.ZERO) == 0)
//                continue;
//            BigDecimal sharePrice = sharePrices.get(position.getSymbol());
//            gain = gain.add(position.gain(sharePrice));
//        }
//        return gain;
//    }
//
//    public BigDecimal gainPercentage(Map<String, BigDecimal> sharePrices) {
//        BigDecimal gainPercentage = BigDecimal.ZERO;
//        try {
//            gainPercentage = gain(sharePrices).divide(costBasis(), MathContext.DECIMAL32)
//                    //multiply by 100 for percentage
//                    .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
//        } catch (ArithmeticException e) {
//            //TODO: Is this acceptable?
//            //pass;
//        }
//        return gainPercentage;
//    }
//
//    public BigDecimal returnsGain(Map<String, BigDecimal> sharePrices) {
//        BigDecimal returnsGain = BigDecimal.ZERO;
//        for (Position position : positions.values()) {
//            BigDecimal sharePrice = sharePrices.get(position.getSymbol());
//            returnsGain = returnsGain.add(position.returnsGain(sharePrice));
//        }
//        return returnsGain;
//    }
//
//    public BigDecimal cashOut() {
//        BigDecimal cashOut = BigDecimal.ZERO;
//        for (Position position : positions.values()) {
//            cashOut = cashOut.add(position.cashOut());
//        }
//        return cashOut;
//    }
//
//    public BigDecimal overallReturn(Map<String, BigDecimal> sharePrices) {
//        return returnsGain(sharePrices).divide(cashOut().negate(), MathContext.DECIMAL32)
//                //multiply by 100 for percentage
//                .multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));


//    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Current Portfolio:").append("\n");
        for (Map.Entry<Fund, Position> entry : positions.entrySet()) {
            sb.append(entry.getKey()).append(" ").append(entry.getValue().shares()).append("\n");
        }
        return sb.toString();
    }
}
