package com.mns.mojoinvest.server.engine.strategy;

import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.shared.params.Strategy2Params;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.logging.Logger;

public class RelativeStrengthCalculator {

    private static final Logger log = Logger.getLogger(RelativeStrengthCalculator.class.getName());

    private final CalculatedValueDao calculatedValueDao;

    @Inject
    public RelativeStrengthCalculator(CalculatedValueDao dao) {
        this.calculatedValueDao = dao;
    }


    public List<Map<String, BigDecimal>> getRelativeStrengthsMA(Collection<Fund> funds, Strategy2Params params, List<LocalDate> dates) {

        log.info("Starting load of calculated values");
        //TODO: Factor calculation of RS to separate class
        Collection<CalculatedValue> ma1s = calculatedValueDao.get(dates, funds,
                "SMA", params.getMa1());
        Collection<CalculatedValue> ma2s = calculatedValueDao.get(dates, funds,
                "SMA", params.getMa2());
        Collection<CalculatedValue> stddevs = calculatedValueDao.get(dates, funds, "RSQUARED", params.getStdDev());
//        Collection<CalculatedValue> stddevs = calculatedValueDao.get(dates, funds, "STDDEV", params.getStdDev());

        log.info("Building intermediate data structures");
        Map<LocalDate, Map<String, BigDecimal>> ma1Map = buildDateCalcValueMap(ma1s);
        Map<LocalDate, Map<String, BigDecimal>> ma2Map = buildDateCalcValueMap(ma2s);
        Map<LocalDate, Map<String, BigDecimal>> stddevMap = buildDateCalcValueMap(stddevs);

        List<Map<String, BigDecimal>> allRs = new ArrayList<Map<String, BigDecimal>>(dates.size());
        for (LocalDate date : dates) {

            Map<String, BigDecimal> rs = new HashMap<String, BigDecimal>();
            if (ma1Map.containsKey(date) && ma2Map.containsKey(date) && stddevMap.containsKey(date)) {
                Map<String, BigDecimal> ma1vals = ma1Map.get(date);
                Map<String, BigDecimal> ma2vals = ma2Map.get(date);
                Map<String, BigDecimal> stddevVals = stddevMap.get(date);
                for (Fund fund : funds) {
                    String symbol = fund.getSymbol();
                    if (!ma1vals.containsKey(symbol)) {
                        log.fine("Unable to calculate RS for " + symbol + " on " + date + " no SMA|" + params.getMa1());
                    } else if (!ma2vals.containsKey(symbol)) {
                        log.fine("Unable to calculate RS for " + symbol + " on " + date + " no SMA|" + params.getMa2());
                    } else if (!stddevVals.containsKey(symbol)) {
                        log.fine("Unable to calculate RS for " + symbol + " on " + date + " no STDDEV|" + params.getStdDev());
                    } else {
                        log.fine("Calculating RS(MA) for " + symbol + " as (" + ma1vals.get(symbol) + " / " +
                                ma2vals.get(symbol) + ") / " + stddevVals.get(symbol));
                        BigDecimal maRatio = ma1vals.get(symbol).divide(ma2vals.get(symbol), RoundingMode.HALF_EVEN);

                        if (stddevVals.get(symbol).compareTo(BigDecimal.ZERO) != 0) {
                            //If the fund price has been flat for the same period as was used to calculate
                            //the standard deviation, the std dev could be 0.
//                            rs.put(symbol, maRatio.divide(stddevVals.get(symbol), RoundingMode.HALF_EVEN));
                            rs.put(symbol, maRatio.multiply(stddevVals.get(symbol)));
//                            rs.put(symbol, maRatio);

                        }
                    }

                }
            }
            allRs.add(rs);
        }
        return allRs;
    }


    public List<Map<String, BigDecimal>> getRelativeStrengthsROC(Collection<Fund> funds, Strategy2Params params, List<LocalDate> dates) {

        List<Map<String, BigDecimal>> allRs = new ArrayList<Map<String, BigDecimal>>(dates.size());
        Collection<CalculatedValue> rocs = calculatedValueDao.get(dates, funds, "ROC", params.getRoc());
        Map<LocalDate, Map<String, BigDecimal>> rocMap = buildDateCalcValueMap(rocs);
        Collection<CalculatedValue> stddevs = calculatedValueDao.get(dates, funds, "RSQUARED", params.getStdDev());
//        Collection<CalculatedValue> stddevs = calculatedValueDao.get(dates, funds, "STDDEV", params.getStdDev());
        Map<LocalDate, Map<String, BigDecimal>> stddevMap = buildDateCalcValueMap(stddevs);

        for (LocalDate date : dates) {

            Map<String, BigDecimal> rs = new HashMap<String, BigDecimal>();
            Map<String, BigDecimal> rocVals = rocMap.get(date);
            Map<String, BigDecimal> stddevVals = stddevMap.get(date);

            if (rocVals == null) {
                log.warning("No ROC values calculated for " + date);
                allRs.add(rs);
                continue;
            }
            if (stddevVals == null) {
                log.warning("No STDDEV values calculated for " + date);
                allRs.add(rs);
                continue;
            }

            for (Fund fund : funds) {
                String symbol = fund.getSymbol();
                if (!rocVals.containsKey(symbol)) {
                    log.fine("Unable to calculate RS for " + symbol + " on " + date + " no ROC|" + params.getRoc());
                } else if (!stddevVals.containsKey(symbol)) {
                    log.fine("Unable to calculate RS for " + symbol + " on " + date + " no STDDEV|" + params.getStdDev());
                } else {
                    log.fine("Calculating RS(ROC) for " + symbol + " as " + rocVals.get(symbol) + " / " + stddevVals.get(symbol));
                    BigDecimal roc = rocVals.get(symbol);

                    if (stddevVals.get(symbol).compareTo(BigDecimal.ZERO) != 0) {
                        //If the fund price has been flat for the same period as was used to calculate
                        //the standard deviation, the std dev will be 0.
//                        rs.put(symbol, roc.divide(stddevVals.get(symbol), RoundingMode.HALF_EVEN));
//                        rs.put(symbol, roc.multiply(stddevVals.get(symbol)));
                        rs.put(symbol, roc);
                    }
                }
            }
            allRs.add(rs);
        }

        return allRs;
    }

    private Map<LocalDate, Map<String, BigDecimal>> buildDateCalcValueMap(Collection<CalculatedValue> vals) {
        Map<LocalDate, Map<String, BigDecimal>> valMap = new HashMap<LocalDate, Map<String, BigDecimal>>();
        for (CalculatedValue val : vals) {
            if (!valMap.containsKey(val.getDate())) {
                valMap.put(val.getDate(), new HashMap<String, BigDecimal>());
            }
            valMap.get(val.getDate()).put(val.getSymbol(), val.getValue());
        }
        return valMap;
    }
}