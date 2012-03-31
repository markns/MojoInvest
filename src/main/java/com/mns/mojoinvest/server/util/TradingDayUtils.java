package com.mns.mojoinvest.server.util;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TradingDayUtils {


//    Actual: paid on the actual day, even if it is a non-business day.
//    Following business day: the payment date is rolled to the next business day.
//    Modified following business day: the payment date is rolled to the next business day, unless doing so would
//      cause the payment to be in the next calendar month, in which case the payment date is rolled to the previous
//      business day. Many institutions have month-end accounting procedures that necessitate this.
//    Previous business day: the payment date is rolled to the previous business day.
//    Modified previous business day: the payment date is rolled to the previous business day, unless doing so
//      would cause the payment to be in the previous calendar month, in which case the payment date is rolled to
//      the next business day. Many institutions have month-end accounting procedures that necessitate this.

    //TODO: Change these methods to accept an enum Month, Week, Day and switch
    //TODO: Maybe add an option to specify stub date

    public static List<LocalDate> getMonthlySeries(LocalDate fromDate, LocalDate toDate, int frequency, boolean forwards) {
        List<LocalDate> dates = new ArrayList<LocalDate>();
        while (!rollBack(toDate).isBefore(fromDate)) {
            dates.add(rollBack(toDate));
            toDate = toDate.minusMonths(frequency);
        }
        if (forwards) {
            //Little bit back to front the logic here
            Collections.reverse(dates);
        }
        return dates;
    }

    public static List<LocalDate> getWeeklySeries(LocalDate fromDate, LocalDate toDate, int frequency, boolean forwards) {
        List<LocalDate> dates = new ArrayList<LocalDate>();
        while (!rollBack(toDate).isBefore(fromDate)) {
            dates.add(rollBack(toDate));
            toDate = toDate.minusWeeks(frequency);
        }
        if (forwards) {
            //Little bit back to front the logic here
            Collections.reverse(dates);
        }
        return dates;
    }

    public static List<LocalDate> getDailySeries(LocalDate fromDate, LocalDate toDate, boolean forwards) {
        List<LocalDate> dates = new ArrayList<LocalDate>();
        while (!rollBack(toDate).isBefore(fromDate)) {
            toDate = rollBack(toDate);
            dates.add(toDate);
            toDate = toDate.minusDays(1);
        }
        if (forwards) {
            //Little bit back to front the logic here
            Collections.reverse(dates);
        }
        return dates;
    }

    public static List<LocalDate> getPreviousDays(LocalDate date, int numDays, boolean inclusive) {
        List<LocalDate> dates = new ArrayList<LocalDate>();
        int count = 0;
        if (inclusive) {
            date = rollBack(date);
            dates.add(date);
            count++;
        }
        while (count < numDays) {
            date = rollBack(date.minusDays(1));
            dates.add(date);
            count++;
        }
        return dates;
    }

    //Always roll back because we might not have the market data if we roll forwards
    public static LocalDate rollBack(LocalDate date) {
        if (date.dayOfWeek().get() == DateTimeConstants.SATURDAY)
            //Recursion is to check that we haven't rolled into a non-weekend holiday
            return rollBack(date.minusDays(1));
        else if (date.dayOfWeek().get() == DateTimeConstants.SUNDAY)
            return rollBack(date.minusDays(2));
        else if (HolidayUtils.isHoliday(date))
            //Recursion is to check for multi-day non-weekend closures eg. 7/11
            return rollBack(date.minusDays(1));
        else
            return date;
    }

    public static LocalDate rollForward(LocalDate date) {
        if (date.dayOfWeek().get() == DateTimeConstants.SATURDAY)
            //Recursion is to check that we haven't rolled into a non-weekend holiday
            return rollForward(date.plusDays(2));
        else if (date.dayOfWeek().get() == DateTimeConstants.SUNDAY)
            return rollForward(date.plusDays(1));
        else if (HolidayUtils.isHoliday(date))
            //Recursion is to check for multi-day non-weekend closures eg. 7/11
            return rollBack(date.plusDays(1));
        else
            return date;
    }
}
