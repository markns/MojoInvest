package com.mns.mojoinvest.server.tools;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.util.QuoteUtils;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.joda.time.LocalDate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolidayFinderUtil {

    public static void main(String[] args) throws Exception {

        Map<LocalDate, MutableInt> freq = new HashMap<LocalDate, MutableInt>();

        List<LocalDate> dates = TradingDayUtils.getDailySeries(
                new LocalDate("2000-01-29"), new LocalDate("2012-07-06"), true);
        for (LocalDate date : dates) {
            freq.put(date, new MutableInt(0));
        }


        String[] files = new String[]{"/Users/marknuttallsmith/Projects/MojoInvest/data/ishares_uk.csv"};
        for (String file : files) {

            CSVReader reader = new CSVReader(new BufferedReader(new FileReader(file)));

            String[] row;
            while ((row = reader.readNext()) != null) {
                if ("symbol".equals(row[0]))
                    continue;
                Quote quote = QuoteUtils.fromStringArray(row);
                freq.get(quote.getDate()).increment();
            }
            reader.close();
        }
        Ordering<LocalDate> valueComparator = Ordering.natural()
                .onResultOf(Functions.forMap(freq))
                .compound(Ordering.natural());


        for (Map.Entry<LocalDate, MutableInt> e : ImmutableSortedMap.copyOf(freq, valueComparator).entrySet()) {
            System.out.println(e.getKey() + "," + e.getValue());
        }

    }

}