package com.mns.mojoinvest.server.engine.calculator;

import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Quote;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.regression.SimpleRegression;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

//TODO: Consider splitting this class into a set of calculator classes
public class CalculationService {

    public static List<CalculatedValue> calculateSMA(List<Quote> quotes, int period) {
        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
        DescriptiveStatistics stats = new DescriptiveStatistics(period);
        for (Quote quote : quotes) {
            stats.addValue(quote.getTrNav().doubleValue());
            if (stats.getN() >= period) {
                CalculatedValue cv = new CalculatedValue(quote.getDate(), quote.getSymbol(),
                        "SMA", period, stats.getMean());
                cvs.add(cv);
            }
        }
        return cvs;
    }

    public static List<CalculatedValue> calculateStandardDeviation(List<Quote> quotes, int period) {
        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
        DescriptiveStatistics stats = new DescriptiveStatistics(period);
        for (Quote quote : quotes) {
            stats.addValue(quote.getTrNav().doubleValue());
            if (stats.getN() >= period) {
                CalculatedValue cv = new CalculatedValue(quote.getDate(), quote.getSymbol(),
                        "STDDEV", period, stats.getStandardDeviation());
                cvs.add(cv);
            }
        }
        return cvs;
    }

    private static BigDecimal HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);

    public static List<CalculatedValue> calculateROC(List<Quote> quotes, int period) {
//        cv = 100 * ((toQuote - fromQuote) / fromQuote)
        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
//        Collections.reverse(quotes);


        for (int i = 0; i + period < quotes.size(); i++) {
            Quote fromQuote = quotes.get(i);
            Quote toQuote = quotes.get(i + period);
            BigDecimal roc = toQuote.getTrNav().subtract(fromQuote.getTrNav())
                    .divide(fromQuote.getTrNav(), MathContext.DECIMAL32)
                    .multiply(HUNDRED).setScale(3, RoundingMode.HALF_EVEN);

            cvs.add(new CalculatedValue(toQuote.getDate().toString(), toQuote.getSymbol(), "ROC", period,
                    roc));
        }
        return cvs;
    }

    public static List<CalculatedValue> calculateRSquared(List<Quote> quotes, int period) {
        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
        int i = 0;
        while (i + period < quotes.size()) {
            SimpleRegression regression = new SimpleRegression();
            int x = 0;
            for (Quote quote : quotes.subList(i, i + period)) {
//                System.out.println(quote.getDate() + "," + quote.getAdjClose().doubleValue());
                regression.addData(x, quote.getTrNav().doubleValue());
                x++;
            }
            Quote lastQuote = quotes.get(i + period);
            double r2 = regression.getRSquare();
//            if (!Double.isNaN(r2) && !Double.isInfinite(r2)) {
//                System.out.println(new BigDecimal(r2, MathContext.DECIMAL32));
//            }
//            System.out.println();
            cvs.add(new CalculatedValue(lastQuote.getDate(), lastQuote.getSymbol(), "RSQUARED", period, r2));
            i++;
        }

        return cvs;
    }

    public static List<CalculatedValue> calculateAlpha(List<Quote> quotes, NavigableMap<LocalDate, BigDecimal> idxReturns,
                                                       int period) {
        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
        NavigableMap<LocalDate, BigDecimal> returns = new TreeMap<LocalDate, BigDecimal>();
        Quote from = null;
        for (Quote to : quotes) {
            if (from != null) {
                returns.put(to.getDate(), percentageReturn(from.getTrNav(), to.getTrNav()));
            }
            from = to;
        }
        for (int i = 1; i + period < quotes.size(); i++) {
            NavigableMap<LocalDate, BigDecimal> returnsPeriod = returns.subMap(quotes.get(i).getDate(), true,
                    quotes.get(i + period).getDate(), false);
            NavigableMap<LocalDate, BigDecimal> idxReturnsPeriod = idxReturns.subMap(quotes.get(i).getDate(), true,
                    quotes.get(i + period).getDate(), false);
            if (returnsPeriod.size() != idxReturnsPeriod.size()) {
                throw new RuntimeException(returnsPeriod.size() + " " + idxReturnsPeriod.size());
            }
            Iterator<BigDecimal> returnsIter = returnsPeriod.values().iterator();
            Iterator<BigDecimal> idxReturnsIter = idxReturnsPeriod.values().iterator();

            SimpleRegression regression = new SimpleRegression();
            while (returnsIter.hasNext() && idxReturnsIter.hasNext()) {
                regression.addData(returnsIter.next().doubleValue(), idxReturnsIter.next().doubleValue());
            }
            double intercept = regression.getIntercept();
            cvs.add(new CalculatedValue(returnsPeriod.lastEntry().getKey(), quotes.get(0).getSymbol(),
                    "ALPHA", period, intercept));
        }
        return cvs;
    }


    private static BigDecimal percentageReturn(BigDecimal from, BigDecimal to) {
        BigDecimal change = to.subtract(from);
        return change.divide(from, MathContext.DECIMAL32);
    }


}
