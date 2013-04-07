package com.mns.mojoinvest.server.pipeline.calculator;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.model.Correlation;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.CorrelationDao;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.pipeline.PipelineHelper;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.joda.time.LocalDate;

import java.util.*;

public class RunCorrelationCalculatorJob extends Job1<String, LocalDate> {


    @Override
    public Value<String> run(LocalDate date) {


        FundDao fundDao = PipelineHelper.getFundDao();
        QuoteDao quoteDao = PipelineHelper.getQuoteDao();
        CorrelationDao correlationDao = PipelineHelper.getCorrelationDao();

        int period = 6;

        List<LocalDate> dates = TradingDayUtils.getDailySeries(date.minusMonths(period), date, true);

        Collection<Fund> funds = fundDao.list();

        SortedMap<String, List<Double>> matrix = new TreeMap<String, List<Double>>();

        for (Fund fund : funds) {

            if (fund.getEarliestQuoteDate() == null ||
                    dates.get(0).isBefore(fund.getEarliestQuoteDate()))
                continue;

            List<Quote> quotes = quoteDao.get(fund, dates);

            List<Double> doubles = new ArrayList<Double>();
            for (Quote quote : quotes) {
                doubles.add(quote.getTrNav().doubleValue());
            }

            matrix.put(fund.getSymbol(), doubles);

        }
        double[][] matrix2 = new double[matrix.size()][dates.size()];


        int column = 0;
        for (String symbol : matrix.keySet()) {
            double[] d = ArrayUtils.toPrimitive(
                    matrix.get(symbol).toArray(new Double[matrix.get(symbol).size()]));

            for (int row = 0; row < d.length; row++) {
                matrix2[row][column] = d[row];
            }
            column++;
        }

        Correlation correlation = new Correlation(date, period, new ArrayList(matrix.keySet()),
                new PearsonsCorrelation().computeCorrelationMatrix(matrix2));

        correlationDao.save(correlation);

        return immediate("Correlation done");
    }
}
