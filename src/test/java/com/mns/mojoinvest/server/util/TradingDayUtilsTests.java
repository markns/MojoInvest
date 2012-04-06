package com.mns.mojoinvest.server.util;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class TradingDayUtilsTests {

    @Test
    public void testGetMonthlySeries() {
        String[] dtStrs = {
                "2011-07-29",
                "2011-06-30",
                "2011-05-30",
                "2011-04-29",
                "2011-03-30",
                "2011-02-28",
                "2011-01-28",
                "2010-12-28",
                "2010-11-26",
                "2010-10-28",
                "2010-09-28",
                "2010-08-27"
        };
        List<LocalDate> sl = loadAList(dtStrs);
        LocalDate queryDate = new LocalDate(2011, 7, 30);
        List<LocalDate> dates = TradingDayUtils.getMonthlySeries(queryDate.minusYears(1), queryDate, 1, false);
        assertEquals(sl, dates);
    }


    @Test
    public void testGetDailySeries() {
        String[] dtStrs = {
                "2011-07-20",
                "2011-07-19",
                "2011-07-18",
                "2011-07-15",
                "2011-07-14",
                "2011-07-13",
                "2011-07-12",
                "2011-07-11",
                "2011-07-08",
                "2011-07-07",
                "2011-07-06",
                "2011-07-05",
                "2011-07-04",
                "2011-07-01"
        };
        List<LocalDate> sl = loadAList(dtStrs);
        List<LocalDate> dates = TradingDayUtils.getDailySeries(new LocalDate(2011, 7, 1), new LocalDate(2011, 7, 20), false);
        assertEquals(sl, dates);
    }

    @Test
    public void testGetPreviousDaysExclusive() {
        String[] dtStrs = {
                "2011-07-18",
                "2011-07-15",
                "2011-07-14",
                "2011-07-13",
                "2011-07-12",
                "2011-07-11",
                "2011-07-08",
                "2011-07-07",
                "2011-07-06",
                "2011-07-05"
        };
        List<LocalDate> sl = loadAList(dtStrs);
        LocalDate queryDate = new LocalDate(2011, 7, 19);
        int numDays = 10;
        boolean includeToday = false;
        List<LocalDate> dates = TradingDayUtils.getPreviousDays(queryDate, numDays, includeToday);
        assertEquals(sl, dates);
    }

    @Test
    public void testGetPreviousDaysInclusive() {
        String[] dtStrs = {
                "2011-07-19",
                "2011-07-18",
                "2011-07-15",
                "2011-07-14",
                "2011-07-13",
                "2011-07-12",
                "2011-07-11",
                "2011-07-08",
                "2011-07-07",
                "2011-07-06"
        };
        List<LocalDate> sl = loadAList(dtStrs);
        LocalDate queryDate = new LocalDate(2011, 7, 19);
        int numDays = 10;
        boolean includeToday = true;
        List<LocalDate> dates = TradingDayUtils.getPreviousDays(queryDate, numDays, includeToday);
        assertEquals(sl, dates);
    }

    @Test
    public void testHolidayRolling() {
//        "2011-04-22", "Good Friday", "US";
        String[] dtStrs = {
                "2011-04-26",
                "2011-04-25",
                "2011-04-21",
                "2011-04-20",
                "2011-04-19"
        };

        List<LocalDate> sl = loadAList(dtStrs);
        List<LocalDate> dates = TradingDayUtils.getDailySeries(new LocalDate(2011, 4, 19), new LocalDate(2011, 4, 26), false);
        assertEquals(sl, dates);
    }

    @Test
    public void testGetEndOfWeekSeries() {

        String[] dtStrs = {
                "2012-04-05", //Good Friday
                "2012-04-13",
                "2012-04-20"
        };

        List<LocalDate> sl = loadAList(dtStrs);
        List<LocalDate> dates = TradingDayUtils.getEndOfWeekSeries(new LocalDate(2012, 4, 1), new LocalDate(2012, 4, 21), 1);
        assertEquals(sl, dates);

    }

    @Test
    public void testGetEndOfWeekSeriesStartsOnHoliday() {

        String[] dtStrs = {
//                "2012-04-06", Good Friday
                "2012-04-13",
                "2012-04-20"
        };

        List<LocalDate> sl = loadAList(dtStrs);
        List<LocalDate> dates = TradingDayUtils.getEndOfWeekSeries(new LocalDate(2012, 4, 6), new LocalDate(2012, 4, 21), 1);
        assertEquals(sl, dates);

    }


    @Test
    public void testMinusMonths() {
        LocalDate date = new LocalDate("2011-11-01");
        LocalDate date2 = date.minusMonths(1);
        assertEquals(new LocalDate("2011-10-01"), date2);
        assertEquals(DateTimeConstants.SATURDAY, date2.dayOfWeek().get());
        date2 = TradingDayUtils.rollBack(date2);
        assertEquals(new LocalDate("2011-09-30"), date2);
        assertEquals(DateTimeConstants.FRIDAY, date2.dayOfWeek().get());
    }

    private static List<LocalDate> loadAList(String[] someStrs) {
        List<LocalDate> newList = new ArrayList<LocalDate>();
        try {
            for (String someStr : someStrs) {
                newList.add(new LocalDate(someStr));
            } // end of the for
        } catch (IllegalArgumentException pe) {
            pe.printStackTrace();
        }
        return newList;
    }


    public void outputDates() {

        List<LocalDate> dates = TradingDayUtils
                .getDailySeries(new LocalDate("2000-01-01"), new LocalDate("2012-01-13"), true);

        int c = 0;
        for (LocalDate date : dates) {
            if (c < 20)
                System.out.print(date + "|");
            else if (c == 20) {
                System.out.println(date);
                c = 0;
            }
            c++;
        }

    }

}
