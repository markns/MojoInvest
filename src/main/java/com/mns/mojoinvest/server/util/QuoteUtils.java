package com.mns.mojoinvest.server.util;

import com.mns.mojoinvest.server.engine.model.Quote;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.*;

import static com.mns.mojoinvest.server.util.DatastoreUtils.forDatastore;

public class QuoteUtils {

    public static String quoteId(String symbol, LocalDate date) {
        return forDatastore(date) + "|" + symbol;
    }

    public static List<Quote> rollMissingQuotes(List<Quote> quotes) {

        sortByDateAsc(quotes);
        List<LocalDate> dates = TradingDayUtils.getDailySeries(quotes.get(0).getDate(),
                quotes.get(quotes.size() - 1).getDate(), true);

        List<Quote> missingQuotes = new ArrayList<Quote>();

        Iterator<Quote> quoteIter = quotes.iterator();
        Quote quote = quoteIter.next();
        Quote previousQuote = null;

        for (LocalDate date : dates) {
            while (date.isAfter(quote.getDate())) {
                previousQuote = quote;
                if (quoteIter.hasNext()) {
                    quote = quoteIter.next();
                } else {
                    break;
                }
            }

            if (date.equals(quote.getDate())) {
                previousQuote = quote;
                if (quoteIter.hasNext()) {
                    quote = quoteIter.next();
                }
            } else {
                missingQuotes.add(rollQuote(previousQuote, date));
            }

        }
        return missingQuotes;


    }


    @Deprecated
    public static List<Quote> getMissingQuotes(LocalDate fromDate, LocalDate toDate, List<Quote> quotes) {

        sortByDateAsc(quotes);
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
//                System.out.println("after " + quote.getId());
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


    public static void sortByDateAsc(List<Quote> quotes) {
        Collections.sort(quotes, new Comparator<Quote>() {
            @Override
            public int compare(Quote q1, Quote q2) {
                return q1.getDate().compareTo(q2.getDate());
            }
        });
    }

    public static void sortByDateDesc(List<Quote> quotes) {
        Collections.sort(quotes, new Comparator<Quote>() {
            @Override
            public int compare(Quote q1, Quote q2) {
                return q2.getDate().compareTo(q1.getDate());
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
        arr[1] = quote.getDate() + "";
        arr[2] = quote.getOpen() + "";
        arr[3] = quote.getHigh() + "";
        arr[4] = quote.getLow() + "";
        arr[5] = quote.getClose() + "";
        arr[6] = quote.getVolume() + "";
        arr[7] = quote.getAdjClose() + "";
        arr[8] = quote.isRolled() + "";
        return arr;
    }
}
