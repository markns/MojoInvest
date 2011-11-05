package com.mns.mojoinvest.server.util;

import au.com.bytecode.opencsv.CSVWriter;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.servlet.FundLoaderServlet;
import com.mns.mojoinvest.server.servlet.HistoricQuoteLoaderServlet;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class YahooHistoricQuoteDownloader {

    private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    private static final Logger log = Logger.getLogger(YahooHistoricQuoteDownloader.class.getName());


    private final FundDao fundDao = new FundDao(ObjectifyService.factory());
    private final QuoteDao quoteDao = new QuoteDao(ObjectifyService.factory());
    private final FundLoaderServlet fundLoader = new FundLoaderServlet(fundDao);
    private final HistoricQuoteLoaderServlet quoteLoader = new HistoricQuoteLoaderServlet(fundDao, quoteDao);

    public static void main(String[] args) {
        YahooHistoricQuoteDownloader downloader = new YahooHistoricQuoteDownloader();
        try {
            List<Quote> quotes = downloader.download();
            downloader.output(quotes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //http://query.yahooapis.com/v1/public/yql?q=select+*+from+yahoo.finance.historicaldata+where+symbol+%3D+%22BHH%22+and+startDate+%3D+%222011-09-17%22+and+endDate+%3D+%222011-10-26%22&env=store://datatables.org/alltableswithkeys&diagnostics=true
    //http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22BHH%22%20and%20startDate%20%3D%20%222009-09-11%22%20and%20endDate%20%3D%20%222010-03-10%22&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys
    private List<Quote> download() throws IOException {

        List<FundLoaderServlet.FundLite> fundLites = fundLoader.scrapeFundList();
        List<Quote> quotes = new ArrayList<Quote>();
        for (FundLoaderServlet.FundLite fundLite : fundLites) {

            log.info("Retrieving quotes for " + fundLite.getSymbol());
            quotes.addAll(quoteLoader.getHistoricQuotes(fundLite.getSymbol(), new LocalDate(2011, 9, 17), new LocalDate()));
        }
        return quotes;
    }

    private void output(List<Quote> quotes) throws IOException {
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(System.out));
        int count = 0;
        for (Quote quote : quotes) {
            String[] data = new String[]{quote.getSymbol(),
                    fmt.print(quote.getDate()),
                    quote.getOpen().toString(),
                    quote.getHigh().toString(),
                    quote.getLow().toString(),
                    quote.getClose().toString(),
                    quote.getVolume().toString(),
                    quote.getAdjClose().toString(),
                    quote.isRolled() + ""};
            writer.writeNext(data);
            count++;
            if (count == 30) {
                writer.flush();
                count = 0;
            }
        }
        writer.close();
    }

}
