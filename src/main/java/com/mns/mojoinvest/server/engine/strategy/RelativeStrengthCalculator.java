package com.mns.mojoinvest.server.engine.strategy;

import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.shared.params.StrategyParams;
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

    public SortedMap<LocalDate, Map<String, BigDecimal>> getRelativeStrengthsMA(Collection<Fund> funds, StrategyParams params,
                                                                                List<LocalDate> dates) {

        log.info("Starting load of calculated values");
        Collection<CalculatedValue> ma1s = calculatedValueDao.get(dates, funds,
                "SMA", params.getMa1());
        Collection<CalculatedValue> ma2s = calculatedValueDao.get(dates, funds,
                "SMA", params.getMa2());

        log.info("Building intermediate data structures");
        Map<LocalDate, Map<String, BigDecimal>> ma1Map = buildDateCalcValueMap(ma1s);
        Map<LocalDate, Map<String, BigDecimal>> ma2Map = buildDateCalcValueMap(ma2s);

        SortedMap<LocalDate, Map<String, BigDecimal>> allRs = new TreeMap<LocalDate, Map<String, BigDecimal>>();
        for (LocalDate date : dates) {

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
        return allRs;
    }


    public SortedMap<LocalDate, Map<String, BigDecimal>> getRelativeStrengthsROC(Collection<Fund> funds, StrategyParams params, List<LocalDate> dates) {

        Collection<CalculatedValue> rocs = calculatedValueDao.get(dates, funds, "ROC", params.getRoc());

        //TODO: This step should be all that is required - no calculation necessary
        Map<LocalDate, Map<String, BigDecimal>> rocMap = buildDateCalcValueMap(rocs);

        SortedMap<LocalDate, Map<String, BigDecimal>> allRs = new TreeMap<LocalDate, Map<String, BigDecimal>>();
        for (LocalDate date : dates) {

            Map<String, BigDecimal> rs = new HashMap<String, BigDecimal>();
            Map<String, BigDecimal> rocVals = rocMap.get(date);

            if (rocVals == null) {
                log.warning(date + " No ROC values calculated");
                allRs.put(date, rs);
                continue;
            }

            for (Fund fund : funds) {
                String symbol = fund.getSymbol();
                if (!rocVals.containsKey(symbol)) {
                    log.fine(date + " Unable to calculate RS(ROC) for " + symbol + " on " + date + " no ROC|" + params.getRoc());
                } else {
                    rs.put(symbol, rocVals.get(symbol));
                }
            }
            allRs.put(date, rs);
        }

        return allRs;
    }

    public SortedMap<LocalDate, Map<String, BigDecimal>> getRelativeStrengthAlpha(Collection<Fund> funds, StrategyParams params, List<LocalDate> dates) {

        Collection<CalculatedValue> alphas = calculatedValueDao.get(dates, funds, "ALPHA", params.getAlpha());
        //TODO: This step should be all that is required - no calculation necessary
        Map<LocalDate, Map<String, BigDecimal>> alphaMap = buildDateCalcValueMap(alphas);

        SortedMap<LocalDate, Map<String, BigDecimal>> allRs = new TreeMap<LocalDate, Map<String, BigDecimal>>();
        for (LocalDate date : dates) {

            Map<String, BigDecimal> rs = new HashMap<String, BigDecimal>();
            Map<String, BigDecimal> alphaVals = alphaMap.get(date);

            if (alphaVals == null) {
                log.warning(date + " No ALPHA values calculated");
                allRs.put(date, rs);
                continue;
            }

            for (Fund fund : funds) {
                String symbol = fund.getSymbol();
                if (!alphaVals.containsKey(symbol)) {
                    log.fine(date + " Unable to calculate RS(ALPHA) for " + symbol + " on " + date + " no ALPHA|" + params.getAlpha());
                } else {
                    rs.put(symbol, alphaVals.get(symbol));
                }
            }
            allRs.put(date, rs);
        }

        return allRs;
    }

    private SortedMap<LocalDate, Map<String, BigDecimal>> adjustRelativeStrengths(SortedMap<LocalDate, Map<String, BigDecimal>> rs,
                                                                                  Collection<Fund> funds, StrategyParams params,
                                                                                  List<LocalDate> dates) {

        Collection<CalculatedValue> stddevs = calculatedValueDao.get(dates, funds, "RSQUARED", params.getStdDev());
//        Collection<CalculatedValue> stddevs = calculatedValueDao.get(dates, funds, "STDDEV", params.getStdDev());
        Map<LocalDate, Map<String, BigDecimal>> stddevMap = buildDateCalcValueMap(stddevs);


        for (LocalDate date : dates) {
            if (stddevMap.containsKey(date)) {
                Map<String, BigDecimal> stddevVals = stddevMap.get(date);
                for (Fund fund : funds) {
                    String symbol = fund.getSymbol();
                    if (!stddevVals.containsKey(symbol)) {
                        log.fine(date + " Unable to calculate RS for " + symbol + " on " + date + " no STDDEV|" + params.getStdDev());

                    }
                    if (stddevVals.get(symbol).compareTo(BigDecimal.ZERO) != 0) {
                        //If the fund price has been flat for the same period as was used to calculate
                        //the standard deviation, the std dev could be 0.
//                            rs.put(symbol, maRatio.divide(stddevVals.get(symbol), RoundingMode.HALF_EVEN));
//                            rs.put(symbol, maRatio.multiply(stddevVals.get(symbol)));

                    }
                }
            }
        }
        return rs;
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