package com.mns.mojoinvest.server.engine.strategy;

import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.servlet.StrategyServlet;
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

    List<Map<String, BigDecimal>> getRelativeStrengths(Collection<Fund> funds, StrategyServlet.Strategy2Params params, List<LocalDate> dates) {
        log.info("Starting load of calculated values");
        //TODO: Factor calculation of RS to separate class
        Collection<CalculatedValue> ma1s = calculatedValueDao.get(dates, funds,
                "SMA", params.getMa1());
        Collection<CalculatedValue> ma2s = calculatedValueDao.get(dates, funds,
                "SMA", params.getMa2());

        Collection<CalculatedValue> stddevs = calculatedValueDao.get(dates, funds,
                "STDDEV", params.getStdDev());

        log.info("Building intermediate data structures");
        //CalculatedValue -> Data|Symbol|Type|Period
        Map<LocalDate, Map<String, BigDecimal>> ma1Map = new HashMap<LocalDate, Map<String, BigDecimal>>();
        for (CalculatedValue ma1 : ma1s) {
            if (!ma1Map.containsKey(ma1.getDate())) {
                ma1Map.put(ma1.getDate(), new HashMap<String, BigDecimal>());
            }
            ma1Map.get(ma1.getDate()).put(ma1.getSymbol(), ma1.getValue());
        }

        Map<LocalDate, Map<String, BigDecimal>> ma2Map = new HashMap<LocalDate, Map<String, BigDecimal>>();
        for (CalculatedValue ma2 : ma2s) {
            if (!ma2Map.containsKey(ma2.getDate())) {
                ma2Map.put(ma2.getDate(), new HashMap<String, BigDecimal>());
            }
            ma2Map.get(ma2.getDate()).put(ma2.getSymbol(), ma2.getValue());
        }

        Map<LocalDate, Map<String, BigDecimal>> stddevMap = new HashMap<LocalDate, Map<String, BigDecimal>>();
        for (CalculatedValue stddev : stddevs) {
            if (!stddevMap.containsKey(stddev.getDate())) {
                stddevMap.put(stddev.getDate(), new HashMap<String, BigDecimal>());
            }
            stddevMap.get(stddev.getDate()).put(stddev.getSymbol(), stddev.getValue());
        }


        List<Map<String, BigDecimal>> allRs = new ArrayList<Map<String, BigDecimal>>(dates.size());
        for (LocalDate rebalanceDate : dates) {

            Map<String, BigDecimal> rs = new HashMap<String, BigDecimal>();
            if (ma1Map.containsKey(rebalanceDate) && ma2Map.containsKey(rebalanceDate)) {
                Map<String, BigDecimal> ma1vals = ma1Map.get(rebalanceDate);
                Map<String, BigDecimal> ma2vals = ma2Map.get(rebalanceDate);
                Map<String, BigDecimal> stddevVals = stddevMap.get(rebalanceDate);
                for (Fund fund : funds) {
                    String symbol = fund.getSymbol();
                    if (!ma1vals.containsKey(symbol)) {
                        log.info("Unable to calculate RS for " + symbol + " on " + rebalanceDate + " no SMA|" + params.getMa1());
                    } else if (!ma2vals.containsKey(symbol)) {
                        log.info("Unable to calculate RS for " + symbol + " on " + rebalanceDate + " no SMA|" + params.getMa2());
                    } else if (!stddevVals.containsKey(symbol)) {
                        log.info("Unable to calculate RS for " + symbol + " on " + rebalanceDate + " no STDDEV|" + params.getStdDev());
                    } else {
                        //Divide by Std Dev
                        rs.put(symbol, ma1vals.get(symbol).divide(ma2vals.get(symbol), RoundingMode.HALF_EVEN)
                                .divide(stddevVals.get(symbol), RoundingMode.HALF_EVEN));
                    }

                }
            }
            allRs.add(rs);
        }
        return allRs;
    }
}