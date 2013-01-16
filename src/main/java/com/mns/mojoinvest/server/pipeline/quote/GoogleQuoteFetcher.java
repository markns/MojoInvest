package com.mns.mojoinvest.server.pipeline.quote;

import au.com.bytecode.opencsv.CSVReader;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GoogleQuoteFetcher {

    private static final Logger log = Logger.getLogger(GoogleQuoteFetcher.class.getName());


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

    public static final DateTimeFormatter urlFormat = DateTimeFormat.forPattern("MMM+dd+yyyy");

    private String getCsv(Fund fund, LocalDate fromDate, LocalDate toDate) throws QuoteFetcherException {
        WebResource r;
        MultivaluedMap<String, String> params;
        try {
            c.setReadTimeout(10000);
            c.setConnectTimeout(10000);

            r = c.resource("http://www.google.co.uk/finance/historical");
            params = new MultivaluedMapImpl();
            params.add("q", "LON:" + fund.getSymbol());
            params.add("ei", "3zv0UKn1GqmKwAPadA");  //?
            params.add("output", "csv");
            params.add("startdate", urlFormat.print(fromDate));
            params.add("enddate", urlFormat.print(toDate));
            log.fine(r.queryParams(params).toString());
            return r.queryParams(params).get(String.class);
        } catch (UniformInterfaceException e) {
            throw new QuoteFetcherException("Unable to fetch " + fund.getSymbol() + " quotes between " +
                    fromDate + " and " + toDate, e);
        }
    }

    public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MMM-yy");

    private static Quote fromStrArr(String symbol, String[] row) {
        //System.out.println(Joiner.on(" - ").join(row));
        return new Quote(symbol,
                fmt.parseLocalDate(row[0]),
                "-".equals(row[1]) ? null : new BigDecimal(row[1]),
                "-".equals(row[2]) ? null : new BigDecimal(row[2]),
                "-".equals(row[3]) ? null : new BigDecimal(row[3]),
                "-".equals(row[4]) ? null : new BigDecimal(row[4]),
                null, null,
                "-".equals(row[5]) ? null : new BigDecimal(row[5]),
                null,
                false);
    }


//    http://www.google.co.uk/finance/historical?cid=12299547&&&num=30&ei=Jzv0UKjOJ6eCwAPUaw&start=60&output=csv

    //http://www.google.co.uk/finance/historical?q=LON:IEER&ei=3zv0UKn1GqmKwAPadA&output=csv
    //http://www.google.co.uk/finance/historical?q=LON:IUSA&ei=3zv0UKn1GqmKwAPadA&output=csv
    //ei=7Tz0UIjEIIOPwAPzZw


}
