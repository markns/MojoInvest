package com.mns.alphaposition.server.util;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.joda.time.LocalDate;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ISharesConvert {

    public static final String DATA_DIR = "C:\\workspace\\Alpha-Position\\data\\";

    private static final String[] iSharesFiles = {
            "DUB_alternatives.csv",
            "DUB_developedequity.csv",
            "DUB_emergingequity.csv",
            "DUB_fixedincome.csv"
    };

    public static void main(String[] args) throws IOException {

        for (String file : iSharesFiles) {
            CSVReader reader = new CSVReader(new FileReader(DATA_DIR + file));

//            Funds,iShares Dow Jones Europe Sustainability Screened(IESE),,,,,iShares Dow Jones Global Sustainability Screened(IGSG),,,,,iShares FTSE EPRA/NAREIT Asia Property Yield Fund(IASP),,,,,iShares FTSE EPRA/NAREIT Developed Markets Property Yield Fund(IWDP),,,,,iShares FTSE EPRA/NAREIT UK Property Fund(IUKP),,,,,iShares FTSE EPRA/NAREIT US Property Yield Fund(IUSP),,,,,iShares FTSE/EPRA European Property Index Fund(IPRP),,,,,iShares FTSE/Macquarie Global Infrastructure 100(INFR),,,,,iShares MSCI Emerging Markets Islamic(ISEM),,,,,iShares MSCI USA Islamic(ISUS),,,,,iShares MSCI World Islamic(ISWD),,,,,iShares S&P Emerging Market Infrastructure(IEMI),,,,,iShares S&P Global Clean Energy(INRG),,,,,iShares S&P Global Timber & Forestry(WOOD),,,,,iShares S&P Global Water(IH2O),,,,,iShares S&P Listed Private Equity(IPRV),,,
//
//            Date,IESE - Index Level,IESE - Fund NAV,IESE - Total Return NAV,IESE - Ex-Dividends,,IGSG - Index Level,IGSG - Fund NAV,IGSG - Total Return NAV,IGSG - Ex-Dividends,,IASP - Index Level,IASP - Fund NAV,IASP - Total Return NAV,IASP - Ex-Dividends,,IWDP - Index Level,IWDP - Fund NAV,IWDP - Total Return NAV,IWDP - Ex-Dividends,,IUKP - Index Level,IUKP - Fund NAV,IUKP - Total Return NAV,IUKP - Ex-Dividends,,IUSP - Index Level,IUSP - Fund NAV,IUSP - Total Return NAV,IUSP - Ex-Dividends,,IPRP - Index Level,IPRP - Fund NAV,IPRP - Total Return NAV,IPRP - Ex-Dividends,,INFR - Index Level,INFR - Fund NAV,INFR - Total Return NAV,INFR - Ex-Dividends,,ISEM - Index Level,ISEM - Fund NAV,ISEM - Total Return NAV,ISEM - Ex-Dividends,,ISUS - Index Level,ISUS - Fund NAV,ISUS - Total Return NAV,ISUS - Ex-Dividends,,ISWD - Index Level,ISWD - Fund NAV,ISWD - Total Return NAV,ISWD - Ex-Dividends,,IEMI - Index Level,IEMI - Fund NAV,IEMI - Total Return NAV,IEMI - Ex-Dividends,,INRG - Index Level,INRG - Fund NAV,INRG - Total Return NAV,INRG - Ex-Dividends,,WOOD - Index Level,WOOD - Fund NAV,WOOD - Total Return NAV,WOOD - Ex-Dividends,,IH2O - Index Level,IH2O - Fund NAV,IH2O - Total Return NAV,IH2O - Ex-Dividends,,IPRV - Index Level,IPRV - Fund NAV,IPRV - Total Return NAV,IPRV - Ex-Dividends
//            29/07/2011,112.249363,23.781514,23.781514,,,1183.732702,24.798190,24.798190,,,2557.450000,23.992816,28.737001,,,2302.060000,20.541479,24.768173,,,1853.590000,4.273545,4.937527,,,2179.490000,20.568098,25.549621,,,2861.000000,27.055335,32.558512,,,10083.191500,22.419678,25.787305,,,1247.518498,22.508121,23.769554,,,1066.786993,25.183034,26.388023,,,1042.722057,23.762402,25.075878,,,2855.397980,23.485007,25.906651,,,1024.352721,8.515444,8.740512,,,1352.946685,16.130766,17.820164,,,2924.818377,24.947361,27.075093,,,123.425111,14.169085,16.358189,

            reader.readNext();
            reader.readNext();

            String[] header = reader.readNext();
            Map<Integer, String> symbols = new HashMap<Integer, String>();
            for (int i = 0; i < header.length; i++) {
                String col = header[i];
                if (col.endsWith(" - Fund NAV")) {
                    symbols.put(i, col.split(" - ")[0]);
                }
            }

            List<QuoteLite> quotes = new ArrayList<QuoteLite>();
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                for (int i = 0; i < nextLine.length; i++) {
                    String[] dateTokens = nextLine[0].split("/");
                    LocalDate date = new LocalDate(Integer.parseInt(dateTokens[2]),
                            Integer.parseInt(dateTokens[1]), Integer.parseInt(dateTokens[0]));
                    if (symbols.containsKey(i)) {
                        if (!nextLine[i].isEmpty() && !"N/A".equals(nextLine[i].trim())) {
                            quotes.add(new QuoteLite(symbols.get(i),
                                date,
                                new BigDecimal(nextLine[i])));
                        }
                    }
                }
            }           //           op      hi      lo      cl      vol     ad
            //"AADR","2010-12-31","29.73","29.91","29.63","29.84","17300","29.82","false"
            //"LQDE","2011-07-29","     ","     ","     ","106.463407","","","false"
            Map<String, CSVWriter> writers = new HashMap<String, CSVWriter>();
            for (QuoteLite quote : quotes) {
                if (!writers.containsKey(quote.symbol)) {
                    writers.put(quote.symbol, new CSVWriter(
                            new FileWriter("C:\\workspace\\Alpha-Position\\src\\test\\resources\\quote\\ishares\\"
                                    + quote.symbol + ".csv")));
                }
                writers.get(quote.symbol).writeNext(new String[]{
                        quote.symbol,
                        quote.date.toString(),
                        "",
                        "",
                        "",
                        quote.close.toString(),
                        "",
                        "",
                        "false"
                });
            }
            for (CSVWriter csvWriter : writers.values()) {
                csvWriter.flush();
                csvWriter.close();
            }
        }

    }

    private static class QuoteLite {

        String symbol;
        LocalDate date;
        BigDecimal close;

        private QuoteLite(String symbol, LocalDate date, BigDecimal close) {
            this.symbol = symbol;
            this.date = date;
            this.close = close;
        }
    }
}
