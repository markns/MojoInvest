package com.mns.mojoinvest.server.engine.calculator;

import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Quote;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.regression.SimpleRegression;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CalculationService {

    public List<CalculatedValue> calculateSMA(List<Quote> quotes, int period) {
        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
        DescriptiveStatistics stats = new DescriptiveStatistics();
        stats.setWindowSize(period);
        for (Quote quote : quotes) {
            stats.addValue(quote.getAdjClose().doubleValue());
            if (stats.getN() >= period) {
                CalculatedValue cv = new CalculatedValue(quote.getDate(), quote.getSymbol(),
                        "SMA", period, stats.getMean());
                cvs.add(cv);
            }
        }
        return cvs;
    }

    public List<CalculatedValue> calculateStandardDeviation(List<Quote> quotes, int period) {
        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
        DescriptiveStatistics stats = new DescriptiveStatistics();
        stats.setWindowSize(period);
        for (Quote quote : quotes) {
            stats.addValue(quote.getAdjClose().doubleValue());
            if (stats.getN() >= period) {
                CalculatedValue cv = new CalculatedValue(quote.getDate(), quote.getSymbol(),
                        "STDDEV", period, stats.getStandardDeviation());
                cvs.add(cv);
            }
        }
        return cvs;
    }

    private static BigDecimal HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);

    public List<CalculatedValue> calculateROC(List<Quote> quotes, int period) {
//        cv = 100 * ((toQuote - fromQuote) / fromQuote)
        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
        Collections.reverse(quotes);


        for (int i = 0; i + period < quotes.size(); i++) {
            Quote toQuote = quotes.get(i);
            Quote fromQuote = quotes.get(i + period);
            BigDecimal roc = toQuote.getAdjClose().subtract(fromQuote.getAdjClose())
                    .divide(fromQuote.getAdjClose(), MathContext.DECIMAL32)
                    .multiply(HUNDRED).setScale(3, RoundingMode.HALF_EVEN);

            cvs.add(new CalculatedValue(toQuote.getDate(), toQuote.getSymbol(), "ROC", period,
                    roc));
        }
        return cvs;
    }

    public Collection<? extends CalculatedValue> calculateRSquared(List<Quote> quotes, int period) {
        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();


        int i = 0;
        while (i + period < quotes.size()) {
            SimpleRegression regression = new SimpleRegression();
            int x = 0;
            for (Quote quote : quotes.subList(i, i + period)) {
//                System.out.println(quote.getDate() + "," + quote.getAdjClose().doubleValue());
                regression.addData(x, quote.getAdjClose().doubleValue());
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


}
