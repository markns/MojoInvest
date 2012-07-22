package com.mns.mojoinvest.server.util;

import org.joda.time.LocalDate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HolidayUtils {

    private static Map<LocalDate, String> holidays = new HashMap<LocalDate, String>();

    static {
        Map<LocalDate, String> aMap = new HashMap<LocalDate, String>();
//        aMap.put(new LocalDate("1993-02-15"), "Washington's Birthday");
//        aMap.put(new LocalDate("1993-04-09"), "Good Friday");
//        aMap.put(new LocalDate("1993-05-31"), "Memorial Day");
//        aMap.put(new LocalDate("1993-07-05"), "Independence Day");
//        aMap.put(new LocalDate("1993-09-06"), "Labor Day");
//        aMap.put(new LocalDate("1993-11-25"), "Thanksgiving Day");
//        aMap.put(new LocalDate("1993-12-24"), "Christmas Day");
//        aMap.put(new LocalDate("1994-02-21"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("1994-04-01"), "Good Friday");
//        aMap.put(new LocalDate("1994-04-27"), "Closed for funeral of former President Richard M. Nixon");
//        aMap.put(new LocalDate("1994-05-30"), "Memorial Day");
//        aMap.put(new LocalDate("1994-07-04"), "Independence Day");
//        aMap.put(new LocalDate("1994-09-05"), "Labor Day");
//        aMap.put(new LocalDate("1994-11-24"), "Thanksgiving Day");
//        aMap.put(new LocalDate("1994-12-26"), "Christmas Day");
//        aMap.put(new LocalDate("1995-01-02"), "New Year's Day");
//        aMap.put(new LocalDate("1995-02-20"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("1995-04-14"), "Good Friday");
//        aMap.put(new LocalDate("1995-05-29"), "Memorial Day");
//        aMap.put(new LocalDate("1995-07-04"), "Independence Day");
//        aMap.put(new LocalDate("1995-09-04"), "Labor Day");
//        aMap.put(new LocalDate("1995-11-23"), "Thanksgiving Day");
//        aMap.put(new LocalDate("1995-12-25"), "Christmas Day");
//        aMap.put(new LocalDate("1996-01-01"), "New Year's Day");
//        aMap.put(new LocalDate("1996-02-19"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("1996-04-05"), "Good Friday");
//        aMap.put(new LocalDate("1996-05-27"), "Memorial Day");
//        aMap.put(new LocalDate("1996-07-04"), "Independence Day");
//        aMap.put(new LocalDate("1996-09-02"), "Labor Day");
//        aMap.put(new LocalDate("1996-11-28"), "Thanksgiving Day");
//        aMap.put(new LocalDate("1996-12-25"), "Christmas Day");
//        aMap.put(new LocalDate("1997-01-01"), "New Year's Day");
//        aMap.put(new LocalDate("1997-02-17"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("1997-03-28"), "Washington's Birthday");
//        aMap.put(new LocalDate("1997-05-26"), "Memorial Day");
//        aMap.put(new LocalDate("1997-07-04"), "Independence Day");
//        aMap.put(new LocalDate("1997-09-01"), "Labor Day");
//        aMap.put(new LocalDate("1997-11-27"), "Thanksgiving Day");
//        aMap.put(new LocalDate("1997-12-25"), "Christmas Day");
//        aMap.put(new LocalDate("1998-01-01"), "New Year's Day");
//        aMap.put(new LocalDate("1998-01-19"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("1998-02-16"), "Washington's Birthday");
//        aMap.put(new LocalDate("1998-04-10"), "Memorial Day");
//        aMap.put(new LocalDate("1998-05-25"), "Independence Day");
//        aMap.put(new LocalDate("1998-07-03"), "Labor Day");
//        aMap.put(new LocalDate("1998-09-07"), "Veterans Day");
//        aMap.put(new LocalDate("1998-11-26"), "Thanksgiving Day");
//        aMap.put(new LocalDate("1998-12-25"), "Christmas Day");
//        aMap.put(new LocalDate("1999-01-01"), "New Year's Day");
//        aMap.put(new LocalDate("1999-01-18"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("1999-02-15"), "Washington's Birthday");
//        aMap.put(new LocalDate("1999-04-02"), "Good Friday");
//        aMap.put(new LocalDate("1999-05-31"), "Memorial Day");
//        aMap.put(new LocalDate("1999-07-05"), "Independence Day");
//        aMap.put(new LocalDate("1999-09-06"), "Labor Day");
//        aMap.put(new LocalDate("1999-11-25"), "Thanksgiving Day");
//        aMap.put(new LocalDate("1999-12-24"), "Christmas Day");
//        aMap.put(new LocalDate("2000-01-17"), "New Year's Day");
//        aMap.put(new LocalDate("2000-02-21"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("2000-04-21"), "Washington's Birthday");
//        aMap.put(new LocalDate("2000-05-29"), "Memorial Day");
//        aMap.put(new LocalDate("2000-07-04"), "Independence Day");
//        aMap.put(new LocalDate("2000-09-04"), "Labor Day");
//        aMap.put(new LocalDate("2000-11-23"), "Thanksgiving Day");
//        aMap.put(new LocalDate("2000-12-25"), "Christmas Day");
//        aMap.put(new LocalDate("2001-01-01"), "New Year's Day");
//        aMap.put(new LocalDate("2001-01-15"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("2001-02-19"), "Washington's Birthday");
//        aMap.put(new LocalDate("2001-04-13"), "Good Friday");
//        aMap.put(new LocalDate("2001-05-28"), "Memorial Day");
//        aMap.put(new LocalDate("2001-07-04"), "Independence Day");
//        aMap.put(new LocalDate("2001-09-03"), "Labor Day");
//        aMap.put(new LocalDate("2001-09-11"), "Closed following the terrorist attack on the World Trade Center");
//        aMap.put(new LocalDate("2001-09-12"), "Closed following the terrorist attack on the World Trade Center");
//        aMap.put(new LocalDate("2001-09-13"), "Closed following the terrorist attack on the World Trade Center");
//        aMap.put(new LocalDate("2001-09-14"), "Veterans Day");
//        aMap.put(new LocalDate("2001-11-22"), "Thanksgiving Day");
//        aMap.put(new LocalDate("2001-12-25"), "Christmas Day");
//        aMap.put(new LocalDate("2002-01-01"), "New Year's Day");
//        aMap.put(new LocalDate("2002-01-21"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("2002-02-18"), "Washington's Birthday");
//        aMap.put(new LocalDate("2002-03-29"), "Good Friday");
//        aMap.put(new LocalDate("2002-05-27"), "Memorial Day");
//        aMap.put(new LocalDate("2002-07-04"), "Independence Day");
//        aMap.put(new LocalDate("2002-09-02"), "Labor Day");
//        aMap.put(new LocalDate("2002-11-28"), "Thanksgiving Day");
//        aMap.put(new LocalDate("2002-12-25"), "Christmas Day");
//        aMap.put(new LocalDate("2003-01-01"), "New Year's Day");
//        aMap.put(new LocalDate("2003-01-20"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("2003-02-17"), "Washington's Birthday");
//        aMap.put(new LocalDate("2003-04-18"), "Good Friday");
//        aMap.put(new LocalDate("2003-05-26"), "Memorial Day");
//        aMap.put(new LocalDate("2003-07-04"), "Independence Day");
//        aMap.put(new LocalDate("2003-09-01"), "Labor Day");
//        aMap.put(new LocalDate("2003-11-27"), "Thanksgiving Day");
//        aMap.put(new LocalDate("2003-12-25"), "Christmas Day");
//        aMap.put(new LocalDate("2004-01-01"), "New Year's Day");
//        aMap.put(new LocalDate("2004-01-19"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("2004-02-16"), "Washington's Birthday");
//        aMap.put(new LocalDate("2004-04-09"), "Good Friday");
//        aMap.put(new LocalDate("2004-05-31"), "Memorial Day");
//        aMap.put(new LocalDate("2004-06-11"), "Closed in observance of the National Day of Mourning for former President Ronald W. Reagan");
//        aMap.put(new LocalDate("2004-07-05"), "Independence Day");
//        aMap.put(new LocalDate("2004-09-06"), "Labor Day");
//        aMap.put(new LocalDate("2004-11-25"), "Thanksgiving Day");
//        aMap.put(new LocalDate("2004-12-24"), "Christmas Day");
//        aMap.put(new LocalDate("2005-01-17"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("2005-02-21"), "Washington's Birthday");
//        aMap.put(new LocalDate("2005-03-25"), "Good Friday");
//        aMap.put(new LocalDate("2005-05-30"), "Memorial Day");
//        aMap.put(new LocalDate("2005-07-04"), "Independence Day");
//        aMap.put(new LocalDate("2005-09-05"), "Labor Day");
//        aMap.put(new LocalDate("2005-11-24"), "Thanksgiving Day");
//        aMap.put(new LocalDate("2005-12-26"), "Christmas Day");
//        aMap.put(new LocalDate("2006-01-02"), "New Year's Day");
//        aMap.put(new LocalDate("2006-01-16"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("2006-02-20"), "Washington's Birthday");
//        aMap.put(new LocalDate("2006-04-14"), "Good Friday");
//        aMap.put(new LocalDate("2006-05-29"), "Memorial Day");
//        aMap.put(new LocalDate("2006-07-04"), "Independence Day");
//        aMap.put(new LocalDate("2006-09-04"), "Labor Day");
//        aMap.put(new LocalDate("2006-11-23"), "Thanksgiving Day");
//        aMap.put(new LocalDate("2006-12-25"), "Christmas Day");
//        aMap.put(new LocalDate("2007-01-01"), "New Year's Day");
//        aMap.put(new LocalDate("2007-01-02"), "Closed in observance of the National Day of Mourning for former President Gerald R. Ford");
//        aMap.put(new LocalDate("2007-01-15"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("2007-02-19"), "Washington's Birthday");
//        aMap.put(new LocalDate("2007-04-06"), "Good Friday");
//        aMap.put(new LocalDate("2007-05-28"), "Memorial Day");
//        aMap.put(new LocalDate("2007-07-04"), "Independence Day");
//        aMap.put(new LocalDate("2007-09-03"), "Labor Day");
//        aMap.put(new LocalDate("2007-11-22"), "Thanksgiving Day");
//        aMap.put(new LocalDate("2007-12-25"), "Christmas Day");
//        aMap.put(new LocalDate("2008-01-01"), "New Year's Day");
//        aMap.put(new LocalDate("2008-01-21"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("2008-02-18"), "Washington's Birthday");
//        aMap.put(new LocalDate("2008-03-21"), "Good Friday");
//        aMap.put(new LocalDate("2008-05-26"), "Memorial Day");
//        aMap.put(new LocalDate("2008-07-04"), "Independence Day");
//        aMap.put(new LocalDate("2008-09-01"), "Labor Day");
//        aMap.put(new LocalDate("2008-11-27"), "Thanksgiving Day");
//        aMap.put(new LocalDate("2008-12-25"), "Christmas Day");
//        aMap.put(new LocalDate("2009-01-01"), "New Year's Day");
//        aMap.put(new LocalDate("2009-01-19"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("2009-02-16"), "Washington's Birthday");
//        aMap.put(new LocalDate("2009-04-10"), "Good Friday");
//        aMap.put(new LocalDate("2009-05-25"), "Memorial Day");
//        aMap.put(new LocalDate("2009-07-03"), "Independence Day");
//        aMap.put(new LocalDate("2009-09-07"), "Labor Day");
//        aMap.put(new LocalDate("2009-11-26"), "Thanksgiving Day");
//        aMap.put(new LocalDate("2009-12-25"), "Christmas Day");
//        aMap.put(new LocalDate("2010-01-01"), "New Year's Day");
//        aMap.put(new LocalDate("2010-01-18"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("2010-02-15"), "Washington's Birthday");
//        aMap.put(new LocalDate("2010-04-02"), "Good Friday");
//        aMap.put(new LocalDate("2010-05-31"), "Memorial Day");
//        aMap.put(new LocalDate("2010-07-05"), "Independence Day");
//        aMap.put(new LocalDate("2010-09-06"), "Labor Day");
//        aMap.put(new LocalDate("2010-11-25"), "Thanksgiving Day");
//        aMap.put(new LocalDate("2010-12-24"), "Christmas Day");
//        aMap.put(new LocalDate("2011-01-17"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("2011-02-21"), "Washington's Birthday");
//        aMap.put(new LocalDate("2011-04-22"), "Good Friday");
//        aMap.put(new LocalDate("2011-05-30"), "Memorial Day");
//        aMap.put(new LocalDate("2011-07-04"), "Independence Day");
//        aMap.put(new LocalDate("2011-09-05"), "Labor Day");
//        aMap.put(new LocalDate("2011-11-24"), "Thanksgiving Day");
//        aMap.put(new LocalDate("2011-12-26"), "Christmas Day");
//        aMap.put(new LocalDate("2012-01-02"), "New Year's Day");
//        aMap.put(new LocalDate("2012-01-16"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("2012-02-20"), "Washington's Birthday");
//        aMap.put(new LocalDate("2012-04-06"), "Good Friday");
//        aMap.put(new LocalDate("2012-05-28"), "Memorial Day");
//        aMap.put(new LocalDate("2012-07-04"), "Independence Day");
//        aMap.put(new LocalDate("2012-09-03"), "Labor Day");
//        aMap.put(new LocalDate("2012-11-22"), "Thanksgiving Day");
//        aMap.put(new LocalDate("2012-12-25"), "Christmas Day");
//        aMap.put(new LocalDate("2013-01-01"), "New Year's Day");
//        aMap.put(new LocalDate("2013-01-21"), "Martin Luther King, Jr. Day");
//        aMap.put(new LocalDate("2013-02-18"), "Washington's Birthday");
//        aMap.put(new LocalDate("2013-03-29"), "Good Friday");
//        aMap.put(new LocalDate("2013-05-27"), "Memorial Day");
//        aMap.put(new LocalDate("2013-07-04"), "Independence Day");
//        aMap.put(new LocalDate("2013-09-02"), "Labor Day");
//        aMap.put(new LocalDate("2013-11-28"), "Thanksgiving Day");
//        aMap.put(new LocalDate("2013-12-25"), "Christmas Day");

        aMap.put(new LocalDate("2004-01-01"), "New Year's Day Have a wonderful new year!");
        aMap.put(new LocalDate("2004-04-09"), "Good Friday");
        aMap.put(new LocalDate("2004-04-12"), "Easter Monday (observed)");
        aMap.put(new LocalDate("2004-05-03"), "May Day (Early May Bank Holiday)");
        aMap.put(new LocalDate("2004-05-31"), "Spring Bank Holiday");
        aMap.put(new LocalDate("2004-08-30"), "Summer Bank Holiday");
        aMap.put(new LocalDate("2004-12-27"), "Christmas (observed)");
        aMap.put(new LocalDate("2004-12-28"), "Boxing Day (observed)");
        aMap.put(new LocalDate("2005-01-03"), "New Year's Day (observed)");
        aMap.put(new LocalDate("2005-03-25"), "Good Friday");
        aMap.put(new LocalDate("2005-03-28"), "Easter Monday (observed)");
        aMap.put(new LocalDate("2005-05-02"), "May Day (Early May Bank Holiday)");
        aMap.put(new LocalDate("2005-05-30"), "Spring Bank Holiday");
        aMap.put(new LocalDate("2005-08-29"), "Summer Bank Holiday");
        aMap.put(new LocalDate("2005-12-26"), "Boxing Day (actual) Christmas (observed)");
        aMap.put(new LocalDate("2005-12-27"), "Boxing Day (observed)");
        aMap.put(new LocalDate("2006-01-02"), "New Year's Day (observed)");
        aMap.put(new LocalDate("2006-04-14"), "Good Friday");
        aMap.put(new LocalDate("2006-04-17"), "Easter Monday (observed)");
        aMap.put(new LocalDate("2006-05-01"), "May Day (Early May Bank Holiday)");
        aMap.put(new LocalDate("2006-05-29"), "Spring Bank Holiday");
        aMap.put(new LocalDate("2006-08-28"), "Summer Bank Holiday");
        aMap.put(new LocalDate("2006-12-25"), "Christmas Merry Christmas!");
        aMap.put(new LocalDate("2006-12-26"), "Boxing Day");
        aMap.put(new LocalDate("2007-01-01"), "New Year's Day Have a wonderful new year!");
        aMap.put(new LocalDate("2007-04-06"), "Good Friday");
        aMap.put(new LocalDate("2007-04-09"), "Easter Monday (observed)");
        aMap.put(new LocalDate("2007-05-07"), "May Day (Early May Bank Holiday)");
        aMap.put(new LocalDate("2007-05-28"), "Spring Bank Holiday");
        aMap.put(new LocalDate("2007-08-27"), "Summer Bank Holiday");
        aMap.put(new LocalDate("2007-12-25"), "Christmas Merry Christmas!");
        aMap.put(new LocalDate("2007-12-26"), "Boxing Day");
        aMap.put(new LocalDate("2008-01-01"), "New Year's Day Have a wonderful new year!");
        aMap.put(new LocalDate("2008-03-21"), "Good Friday");
        aMap.put(new LocalDate("2008-03-24"), "Easter Monday (observed)");
        aMap.put(new LocalDate("2008-05-05"), "May Day (Early May Bank Holiday)");
        aMap.put(new LocalDate("2008-05-26"), "Spring Bank Holiday");
        aMap.put(new LocalDate("2008-08-25"), "Summer Bank Holiday");
        aMap.put(new LocalDate("2008-12-25"), "Christmas Merry Christmas!");
        aMap.put(new LocalDate("2008-12-26"), "Boxing Day");
        aMap.put(new LocalDate("2009-01-01"), "New Year's Day Have a wonderful new year!");
        aMap.put(new LocalDate("2009-04-10"), "Good Friday");
        aMap.put(new LocalDate("2009-04-13"), "Easter Monday (observed)");
        aMap.put(new LocalDate("2009-05-04"), "May Day (Early May Bank Holiday)");
        aMap.put(new LocalDate("2009-05-25"), "Spring Bank Holiday");
        aMap.put(new LocalDate("2009-08-31"), "Summer Bank Holiday");
        aMap.put(new LocalDate("2009-12-25"), "Christmas Merry Christmas!");
        aMap.put(new LocalDate("2009-12-28"), "Boxing Day (observed)");
        aMap.put(new LocalDate("2011-01-03"), "New Year's Day (observed)");
        aMap.put(new LocalDate("2011-04-22"), "Good Friday");
        aMap.put(new LocalDate("2011-04-25"), "Easter Monday (observed)");
        aMap.put(new LocalDate("2011-04-29"), "Royal Wedding");
        aMap.put(new LocalDate("2011-05-02"), "May Day (Early May Bank Holiday)");
        aMap.put(new LocalDate("2011-05-30"), "Spring Bank Holiday");
        aMap.put(new LocalDate("2011-08-29"), "Summer Bank Holiday");
        aMap.put(new LocalDate("2011-12-26"), "Boxing Day (actual) Christmas (observed)");
        aMap.put(new LocalDate("2011-12-27"), "Boxing Day (observed)");
        aMap.put(new LocalDate("2012-01-02"), "New Year's Day (observed)");
        aMap.put(new LocalDate("2012-04-06"), "Good Friday");
        aMap.put(new LocalDate("2012-04-09"), "Easter Monday (observed)");
        aMap.put(new LocalDate("2012-05-07"), "May Day (Early May Bank Holiday)");
        aMap.put(new LocalDate("2012-06-04"), "Spring Bank Holiday");
        aMap.put(new LocalDate("2012-06-05"), "Diamond Jubilee of Queen Elizabeth II Jubilee Woods project");
        aMap.put(new LocalDate("2012-08-27"), "Summer Bank Holiday");
        aMap.put(new LocalDate("2012-12-25"), "Christmas Merry Christmas!");
        aMap.put(new LocalDate("2012-12-26"), "Boxing Day");
        aMap.put(new LocalDate("2013-01-01"), "New Year's Day Have a wonderful new year!");
        aMap.put(new LocalDate("2013-03-29"), "Good Friday");
        aMap.put(new LocalDate("2013-04-01"), "April Fools Day Easter Monday (observed)");
        aMap.put(new LocalDate("2013-05-06"), "May Day (Early May Bank Holiday)");
        aMap.put(new LocalDate("2013-05-27"), "Spring Bank Holiday");
        aMap.put(new LocalDate("2013-08-26"), "Summer Bank Holiday");
        aMap.put(new LocalDate("2013-12-25"), "Christmas Merry Christmas!");
        aMap.put(new LocalDate("2013-12-26"), "Boxing Day");
        aMap.put(new LocalDate("2014-01-01"), "New Year's Day Have a wonderful new year!");
        aMap.put(new LocalDate("2014-04-18"), "Good Friday");
        aMap.put(new LocalDate("2014-04-21"), "Easter Monday (observed) Queen Elizabeth's Birthday");
        aMap.put(new LocalDate("2014-05-05"), "May Day (Early May Bank Holiday)");
        aMap.put(new LocalDate("2014-05-26"), "Spring Bank Holiday");
        aMap.put(new LocalDate("2014-08-25"), "Summer Bank Holiday");
        aMap.put(new LocalDate("2014-12-25"), "Christmas Merry Christmas!");
        aMap.put(new LocalDate("2014-12-26"), "Boxing Day");


        holidays = Collections.unmodifiableMap(aMap);
    }

    public static boolean isHoliday(LocalDate date) {
        return holidays.containsKey(date);
    }

    public static String get(LocalDate date) {
        return holidays.get(date);
    }
}
