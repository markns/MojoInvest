package com.mns.mojoinvest.server.engine.calculator;

import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.params.Params;
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

    public SortedMap<String, Map<String, BigDecimal>> getRelativeStrengthsMA(Collection<Fund> funds, Params params,
                                                                             List<LocalDate> dates) {

        log.fine("Starting load of ma1 calculated values");
        long start = System.currentTimeMillis();
        Map<String, Map<String, CalculatedValue>> ma1s = calculatedValueDao.get(funds, "SMA", params.getMa1());
        log.info("Loading " + ma1s.size() + " ma1s from blobstore took " + (System.currentTimeMillis() - start));

        log.fine("Starting load of ma2 calculated values");
        start = System.currentTimeMillis();
        Map<String, Map<String, CalculatedValue>> ma2s = calculatedValueDao.get(funds, "SMA", params.getMa2());
        log.info("Loading " + ma2s.size() + " ma2s from blobstore took " + (System.currentTimeMillis() - start));

        log.fine("Building intermediate data structures");
        Map<String, Map<String, BigDecimal>> ma1Map = buildDateCalcValueMap(ma1s);
        Map<String, Map<String, BigDecimal>> ma2Map = buildDateCalcValueMap(ma2s);
        log.fine("Finished building ma1 & ma2 maps");
//
        SortedMap<String, Map<String, BigDecimal>> allRs = new TreeMap<String, Map<String, BigDecimal>>();
        for (LocalDate localdate : dates) {
            String date = localdate.toString();
            Map<String, BigDecimal> rs = new HashMap<String, BigDecimal>();
            if (ma1Map.containsKey(date) && ma2Map.containsKey(date)) {
                Map<String, BigDecimal> ma1vals = ma1Map.get(date);
                Map<String, BigDecimal> ma2vals = ma2Map.get(date);

                for (Fund fund : funds) {
                    String symbol = fund.getSymbol();
                    if (!ma1vals.containsKey(symbol)) {
                        log.fine(date + " Unable to calculate RS for " + symbol + " on " + date + " no SMA|" + params.getMa1());
                    } else if (!ma2vals.containsKey(symbol)) {
                        log.fine(date + " Unable to calculate RS for " + symbol + " on " + date + " no SMA|" + params.getMa2());
                    } else {
                        rs.put(symbol, ma1vals.get(symbol).divide(ma2vals.get(symbol), RoundingMode.HALF_EVEN));
                    }

                }
            }
            allRs.put(date, rs);
        }
        log.fine("Building allRs map took " + (System.currentTimeMillis() - start));
        return allRs;
    }


    public SortedMap<String, Map<String, BigDecimal>> getRelativeStrengthsROC(Collection<Fund> funds, Params params, List<LocalDate> dates) {
        Map<String, Map<String, CalculatedValue>> rocs = calculatedValueDao.get(funds, "ROC", params.getRoc());
//        Map<String, Map<String, CalculatedValue>> rocs2 = calculatedValueDao.get(funds, "ROC", 4);
        SortedMap<String, Map<String, BigDecimal>> strengths = buildDateCalcValueMap(rocs);
//        SortedMap<String, Map<String, BigDecimal>> strengths2 = buildDateCalcValueMap(rocs2);
//        for (Map.Entry<String, Map<String, BigDecimal>> e : strengths.entrySet()) {
//            Map<String, BigDecimal> symbolMap = strengths2.get(e.getKey());
//            for (String s : e.getValue().keySet()) {
//                BigDecimal s1 = e.getValue().get(s);
//                BigDecimal s2 = symbolMap.get(s);
//                e.getValue().put(s, s1.add(s2));
//            }
//        }

        for (LocalDate localDate : dates) {
            String date = localDate.toString();
            if (strengths.get(date) == null) {
                log.warning(date + " No ROC values calculated");
                strengths.put(date, new HashMap<String, BigDecimal>());
            }
        }
        return strengths;
    }

    public SortedMap<String, Map<String, BigDecimal>> getRelativeStrengthAlpha(Collection<Fund> funds, Params params, List<LocalDate> dates) {
        Map<String, Map<String, CalculatedValue>> alphas = calculatedValueDao.get(funds, "ALPHA", params.getAlpha());
        SortedMap<String, Map<String, BigDecimal>> strengths = buildDateCalcValueMap(alphas);
        for (LocalDate localDate : dates) {
            String date = localDate.toString();
            if (strengths.get(date) == null) {
                log.warning(date + " No ALPHA values calculated");
                strengths.put(date, new HashMap<String, BigDecimal>());
            }
        }
        return strengths;
    }

    public SortedMap<String, Map<String, BigDecimal>> adjustRelativeStrengths(SortedMap<String, Map<String, BigDecimal>> unadjusted,
                                                                              Collection<Fund> funds, Params params,
                                                                              List<LocalDate> dates) {

        SortedMap<String, Map<String, BigDecimal>> adjusted = new TreeMap<String, Map<String, BigDecimal>>();
        log.fine("Starting load of stdDev calculated values");
        Map<String, Map<String, CalculatedValue>> stddevs = calculatedValueDao.get(funds, "STDDEV", params.getStdDev());
        log.fine("Loaded " + stddevs.size() + " ma1s from datastore");

        log.fine("Building intermediate data structure");
        Map<String, Map<String, BigDecimal>> stddevMap = buildDateCalcValueMap(stddevs);

        BigDecimal scaling = new BigDecimal("1");

        long start = System.currentTimeMillis();

        for (String date : unadjusted.keySet()) {
            Map<String, BigDecimal> adjustedDate = new HashMap<String, BigDecimal>();
            for (String symbol : unadjusted.get(date).keySet()) {
                if (stddevMap.containsKey(date) && stddevMap.get(date).containsKey(symbol)) {
                    BigDecimal stdDev = stddevMap.get(date).get(symbol);
                    //if stdDev is 0 (because there has been no variation in price!), return unajusted rs
                    if (stdDev.compareTo(BigDecimal.ZERO) == 0) {
                        adjustedDate.put(symbol, unadjusted.get(date).get(symbol));
                    } else {
                        adjustedDate.put(symbol, unadjusted.get(date).get(symbol)
                                .divide(stdDev.multiply(scaling), RoundingMode.HALF_EVEN));
                    }
                } else {
                    log.warning(date + " Unable to calculate adjusted RS for " + symbol + " on " + date + " no STDDEV|" + params.getStdDev());
                }
            }
            adjusted.put(date, adjustedDate);
        }
        log.fine("Building adjusted RS took " + (System.currentTimeMillis() - start));
        return adjusted;
    }


    public SortedMap<String, Map<String, BigDecimal>> buildDateCalcValueMap(Map<String, Map<String, CalculatedValue>> vals) {
        SortedMap<String, Map<String, BigDecimal>> dateValMap = new TreeMap<String, Map<String, BigDecimal>>();
        for (Map.Entry<String, Map<String, CalculatedValue>> e : vals.entrySet()) {
            String symbol = e.getKey();
            for (String date : e.getValue().keySet()) {
                if (!dateValMap.containsKey(date)) {
                    dateValMap.put(date, new HashMap<String, BigDecimal>());
                }
                dateValMap.get(date).put(symbol, e.getValue().get(date).getValue());
            }
        }
        return dateValMap;
    }
}