package com.mns.mojoinvest.server.util;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DatastoreUtils {

    public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static String forDatastore(LocalDate date) {
        return fmt.print(date);
    }


}
