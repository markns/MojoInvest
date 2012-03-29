package com.mns.mojoinvest.server.engine.calculator;

import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Quote;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
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

    public CalculatedValue calculateROC(Quote fromQuote, Quote toQuote, int period) {
//        cv = 100 * ((toQuote - fromQuote) / fromQuote)

        BigDecimal roc = toQuote.getAdjClose().subtract(fromQuote.getAdjClose())
                .divide(fromQuote.getAdjClose(), MathContext.DECIMAL32)
                .multiply(HUNDRED).setScale(3, RoundingMode.HALF_EVEN);
        return new CalculatedValue(toQuote.getDate(), toQuote.getSymbol(), "ROC", period,
                roc);
    }


}
