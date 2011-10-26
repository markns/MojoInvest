package com.mns.mojoinvest.server.util;

import com.mns.mojoinvest.server.engine.model.Quote;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.*;

public class QuoteUtils {


    public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static String forDatastore(LocalDate date) {
        return fmt.print(date);
    }

    public static String quoteId(String symbol, LocalDate date) {
        return forDatastore(date) + " " + symbol;
    }

    //TODO : tidy this method up
    public static List<Quote> getMissingQuotes(LocalDate fromDate, LocalDate toDate, List<Quote> quotes) {

        sortByDate(quotes);
        List<LocalDate> dates = TradingDayUtils.getDailySeries(fromDate, toDate, true);

        List<Quote> missingQuotes = new ArrayList<Quote>();
        Iterator<Quote> quoteIter = quotes.iterator();
        Quote quote = quoteIter.next();
        Quote earliestQuote = quote;
        Quote previousQuote = null;

        for (LocalDate date : dates) {
            if (date.isBefore(earliestQuote.getDate())) {
//                System.out.println("before");
                missingQuotes.add(rollQuote(quote, date));
                continue;
            }
            while (date.isAfter(quote.getDate())) {
//                System.out.println("after " + quote.getDate());
                previousQuote = quote;
                if (quoteIter.hasNext()) {
                    quote = quoteIter.next();
                } else {
                    break;
                }
            }

            if (date.equals(quote.getDate())) {
//                System.out.println("equals");
                previousQuote = quote;
                if (quoteIter.hasNext()) {
                    quote = quoteIter.next();
                }
            } else {
//                System.out.println("added missing " + previousQuote);
                missingQuotes.add(rollQuote(previousQuote, date));
            }

        }
        return missingQuotes;
    }

    private static Quote rollQuote(Quote quote, LocalDate date) {
        return new Quote(quote.getSymbol(), date, quote.getOpen(),
                quote.getHigh(), quote.getLow(), quote.getClose(), null, null, quote.getVolume(),
                quote.getAdjClose(), true);
    }


    public static void sortByDate(List<Quote> quotes) {
        Collections.sort(quotes, new Comparator<Quote>() {
            @Override
            public int compare(Quote q1, Quote q2) {
                return q1.getDate().compareTo(q2.getDate());
            }
        });
    }

    public static Quote fromStringArray(String[] row) {
        return new Quote(row[0],
                new LocalDate(row[1]),
                row[2].isEmpty() ? null : new BigDecimal(row[2]),
                row[3].isEmpty() ? null : new BigDecimal(row[3]),
                row[4].isEmpty() ? null : new BigDecimal(row[4]),
                row[5].isEmpty() ? null : new BigDecimal(row[5]),
                null, null, row[6].isEmpty() ? null : new BigDecimal(row[6]),
                row[7].isEmpty() ? null : new BigDecimal(row[7]),
                false);
    }


    public static String[] toStringArray(Quote quote) {
        String[] arr = new String[9];
        arr[0] = quote.getSymbol();
        arr[1] = quote.getDate().toString();
        arr[2] = quote.getOpen().toString();
        arr[3] = quote.getHigh().toString();
        arr[4] = quote.getLow().toString();
        arr[5] = quote.getClose().toString();
        arr[6] = quote.getVolume().toString();
        arr[7] = quote.getAdjClose().toString();
        arr[8] = quote.isRolled() + "";
        return arr;
    }
}
