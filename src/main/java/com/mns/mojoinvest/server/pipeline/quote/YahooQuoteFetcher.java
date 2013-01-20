package com.mns.mojoinvest.server.pipeline.quote;

import au.com.bytecode.opencsv.CSVReader;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.util.QuoteUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.joda.time.LocalDate;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class YahooQuoteFetcher {

    private final Client c = Client.create();

    public List<Quote> run(Fund fund, LocalDate fromDate, LocalDate toDate)
            throws QuoteFetcherException {

        String csv = getCsv(fund, fromDate, toDate);
        CSVReader reader = new CSVReader(new StringReader(csv));
        List<Quote> quotes = new ArrayList<Quote>();
        try {
            String[] nextLine;
            reader.readNext();//discard header
            while ((nextLine = reader.readNext()) != null) {
                quotes.add(fromStrArr(fund.getSymbol(), nextLine));
            }
        } catch (IOException e) {
            throw new QuoteFetcherException("Unable to parse quotes for " + fund + ".\nCsv data:\n" +
                    csv, e);
        }

        return quotes;
    }


    private String getCsv(Fund fund, LocalDate fromDate, LocalDate toDate) throws QuoteFetcherException {
        WebResource r;
        MultivaluedMap<String, String> params;
        try {
            c.setReadTimeout(10000);
            c.setConnectTimeout(10000);


//            http://ichart.finance.yahoo.com/table.csv?s=ITWN.L&d=9&e=20&f=2012&g=d&a=9&b=31&c=2005&ignore=.csv

            r = c.resource("http://ichart.finance.yahoo.com/table.csv");
            params = new MultivaluedMapImpl();
            params.add("s", fund.getSymbol() + ".L");
            params.add("g", "d");
            params.add("a", fromDate.getMonthOfYear() - 1 + "");
            params.add("b", fromDate.getDayOfMonth() + "");
            params.add("c", fromDate.getYear() + "");
            params.add("d", toDate.getMonthOfYear() - 1 + "");//yahoo finance indexes to 0
            params.add("e", toDate.getDayOfMonth() + "");
            params.add("f", toDate.getYear() + "");
            params.add("ignore", ".csv");
            return r.queryParams(params).get(String.class);
        } catch (UniformInterfaceException e) {
            throw new QuoteFetcherException("Unable to fetch " + fund.getSymbol() + " quotes between " +
                    fromDate + " and " + toDate, e);
        }
    }

    private static Quote fromStrArr(String symbol, String[] row) {
        return QuoteUtils.fromStringArray(row);
    }
}
