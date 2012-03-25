package com.mns.mojoinvest.server.tools;

import au.com.bytecode.opencsv.CSVWriter;
import com.mns.mojoinvest.server.engine.calculator.CalculationService;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.InMemoryFundDao;
import com.mns.mojoinvest.server.engine.model.dao.InMemoryQuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.util.QuoteUtils;
import org.joda.time.LocalDate;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SMACalculatorTool {

    public static void main(String[] args) throws IOException {
        SMACalculatorTool tool = new SMACalculatorTool();
        tool.run();
    }

    private final QuoteDao quoteDao =
            new InMemoryQuoteDao("data/etf_international_quotes.csv");
    private final InMemoryFundDao fundDao =
            new InMemoryFundDao("data/etf_international_funds.csv");

    private final CalculationService service = new CalculationService(quoteDao);

    private static final String outfile = "data/etf_international_cvs.csv";

    private void run() throws IOException {

        CSVWriter writer = new CSVWriter(new FileWriter(outfile));

        for (Fund fund : fundDao.getAll()) {

            List<Quote> quotes = quoteDao.query(fund.getSymbol());
            QuoteUtils.sortByDateAsc(quotes);
            LocalDate earliest = quotes.get(0).getDate();
            LocalDate latest = quotes.get(quotes.size() - 1).getDate();

            List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();
            for (int period : Arrays.asList(4, 12, 26, 40, 52)) {
                cvs.addAll(service.calculateSMA(fund, earliest, latest, period));
                cvs.addAll(service.calculateSMA(fund, earliest, latest.minusDays(1), period));
                cvs.addAll(service.calculateSMA(fund, earliest, latest.minusDays(2), period));
                cvs.addAll(service.calculateSMA(fund, earliest, latest.minusDays(3), period));
                cvs.addAll(service.calculateSMA(fund, earliest, latest.minusDays(4), period));
            }

            for (CalculatedValue cv : cvs) {
                writer.writeNext(cv.toStrArr());
            }
            writer.flush();
        }
        writer.close();

    }

}
