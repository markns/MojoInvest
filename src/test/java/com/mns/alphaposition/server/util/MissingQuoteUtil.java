package com.mns.alphaposition.server.util;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.mns.alphaposition.server.engine.model.Quote;
import org.joda.time.LocalDate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MissingQuoteUtil {


//BARL,Morgan Stanley S&P 500 Crude Oil ETN,Specialty - Natural Resources,Morgan Stanley,2011-06-28
//PRN,PowerShares Dynamic Industrials,Specialty - Unaligned,PowerShares,2006-10-12
//PXMG,PowerShares Fundamental Pure Mid Growth,Growth,PowerShares,2005-03-03
//RTL,iShares FTSE NAREIT Retail Cp Idx,Specialty - Real Estate,iShares,2007-05-01


    public static void main(String[] args) {

        try {
            CSVReader reader = new CSVReader(new BufferedReader(new FileReader("data/etf.csv")));

            CSVWriter writer = new CSVWriter(new BufferedWriter(new FileWriter("data/missingquotes.csv")));
            for (String[] row : reader.readAll()) {

                String symbol = row[0];
                LocalDate inception = new LocalDate(row[4]);

                System.out.print(symbol + " " + inception + " ... ");

                CSVReader quoteReader = new CSVReader(new BufferedReader(
                        new FileReader("/Users/marknuttallsmith/Projects/ETFData/historical/" + symbol + ".csv")));

                List<Quote> quotes = new ArrayList<Quote>();
                for (String[] quoteRow : quoteReader.readAll()) {
                    quotes.add(QuoteUtils.fromStringArray(quoteRow));
                }
                List<Quote> missing = QuoteUtils.getMissingQuotes(inception, new LocalDate(2011, 9, 16), quotes);
                System.out.println(missing.size() + " missing quotes");
                for (Quote quote : missing) {
                    writer.writeNext(QuoteUtils.toStringArray(quote));
                }

                quoteReader.close();
            }

            reader.close();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

