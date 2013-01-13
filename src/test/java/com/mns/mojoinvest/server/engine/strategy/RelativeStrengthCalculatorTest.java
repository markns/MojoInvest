package com.mns.mojoinvest.server.engine.strategy;

import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.calculator.RelativeStrengthCalculator;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.blobstore.BlobstoreCalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.objectify.ObjectifyEntryRecordDao;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RelativeStrengthCalculatorTest {

    private CalculatedValueDao cvDao = new BlobstoreCalculatedValueDao(
            new ObjectifyEntryRecordDao(ObjectifyService.factory()));


    @Test
    public void testBuildDateCalcValueMap() throws Exception {
        List<LocalDate> dates = TradingDayUtils.getEndOfWeekSeries(new LocalDate("1990-01-01"), new LocalDate("2012-01-01"), 1);
        List<String> symbols = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I");
        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
        for (LocalDate date : dates) {
            for (String symbol : symbols) {
                cvs.add(new CalculatedValue(date, symbol, "MA", 1, BigDecimal.ZERO));
            }
        }
        System.out.println(cvs.size());
        RelativeStrengthCalculator calc = new RelativeStrengthCalculator(cvDao);
        long start = System.currentTimeMillis();
//        calc.buildDateCalcValueMap(cvs);
        System.out.println("Build calc map took " + (System.currentTimeMillis() - start));
    }
}
