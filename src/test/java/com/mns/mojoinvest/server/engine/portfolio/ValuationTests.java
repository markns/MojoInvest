package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ValuationTests {
    public static final BigDecimal COMMISSION = new BigDecimal("12.95");


    public void setUp() throws PortfolioException {

//
//        CSVReader reader = new CSVReader(new FileReader("yourfile.csv"));
//        String [] nextLine;
//        while ((nextLine = reader.readNext()) != null) {
//            // nextLine[] is an array of values from the line
//            System.out.println(nextLine[0] + nextLine[1] + "etc...");
//        }
//        CSVWriter writer = new CSVWriter(new FileWriter("yourfile.csv"), '\t');
//        // feed in your array (or convert your data to an array)
//        String[] entries = "first#second#third".split("#");
//        writer.writeNext(entries);
//        writer.close();


        List<Quote> quotes = new ArrayList<Quote>();
        List<Fund> funds = new ArrayList<Fund>();
        List<Transaction> transactions = new ArrayList<Transaction>();
        Fund AGA = new Fund("AGA", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        funds.add(AGA);
        quotes.add(new Quote("AGA", new LocalDate("2009-01-28"), new BigDecimal("44.7300")));
        quotes.add(new Quote("AGA", new LocalDate("2009-02-25"), new BigDecimal("50.7500")));
        quotes.add(new Quote("AGA", new LocalDate("2009-02-11"), new BigDecimal("47.4500")));
        new BuyTransaction(AGA, new LocalDate("2009-01-28"), new BigDecimal("74"), new BigDecimal("44.7300"), COMMISSION);
        new SellTransaction(AGA, new LocalDate("2009-02-27"), new BigDecimal("74"), new BigDecimal("51.5600"), COMMISSION);
        Fund PXR = new Fund("PXR", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        funds.add(AGA);
        quotes.add(new Quote("PXR", new LocalDate("2009-08-26"), new BigDecimal("38.4800")));
        quotes.add(new Quote("PXR", new LocalDate("2009-08-12"), new BigDecimal("37.6681")));
        new BuyTransaction(PXR, new LocalDate("2009-07-30"), new BigDecimal("68"), new BigDecimal("36.8200"), COMMISSION);
        new SellTransaction(PXR, new LocalDate("2009-08-28"), new BigDecimal("68"), new BigDecimal("38.2100"), COMMISSION);
        Fund DEE = new Fund("DEE", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("DEE", new LocalDate("2009-06-03"), new BigDecimal("58.1500")));
        quotes.add(new Quote("DEE", new LocalDate("2009-05-20"), new BigDecimal("67.0360")));
        quotes.add(new Quote("DEE", new LocalDate("2009-06-17"), new BigDecimal("55.8200")));
        quotes.add(new Quote("DEE", new LocalDate("2009-07-01"), new BigDecimal("61.7700")));
        quotes.add(new Quote("DEE", new LocalDate("2009-07-29"), new BigDecimal("68.9800")));
        quotes.add(new Quote("DEE", new LocalDate("2009-05-06"), new BigDecimal("74.7400")));
        quotes.add(new Quote("DEE", new LocalDate("2009-03-25"), new BigDecimal("78.1999")));
        quotes.add(new Quote("DEE", new LocalDate("2009-07-15"), new BigDecimal("69.2900")));
        quotes.add(new Quote("DEE", new LocalDate("2009-04-22"), new BigDecimal("88.5500")));
        quotes.add(new Quote("DEE", new LocalDate("2009-03-11"), new BigDecimal("97.7000")));
        quotes.add(new Quote("DEE", new LocalDate("2009-04-08"), new BigDecimal("81.5100")));
        new BuyTransaction(DEE, new LocalDate("2009-02-27"), new BigDecimal("45"), new BigDecimal("91.5000"), COMMISSION);
        new SellTransaction(DEE, new LocalDate("2009-07-30"), new BigDecimal("45"), new BigDecimal("64.4800"), COMMISSION);
        Fund DTO = new Fund("DTO", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("DTO", new LocalDate("2009-06-03"), new BigDecimal("81.7600")));
        quotes.add(new Quote("DTO", new LocalDate("2009-05-20"), new BigDecimal("110.9600")));
        quotes.add(new Quote("DTO", new LocalDate("2009-06-17"), new BigDecimal("70.9200")));
        quotes.add(new Quote("DTO", new LocalDate("2009-07-01"), new BigDecimal("76.3200")));
        quotes.add(new Quote("DTO", new LocalDate("2009-07-29"), new BigDecimal("91.8000")));
        quotes.add(new Quote("DTO", new LocalDate("2009-05-06"), new BigDecimal("140.6800")));
        quotes.add(new Quote("DTO", new LocalDate("2009-07-15"), new BigDecimal("91.9200")));
        quotes.add(new Quote("DTO", new LocalDate("2009-04-22"), new BigDecimal("188.9700")));
        quotes.add(new Quote("DTO", new LocalDate("2009-04-08"), new BigDecimal("165.1499")));
        new BuyTransaction(DTO, new LocalDate("2009-03-30"), new BigDecimal("21"), new BigDecimal("176.8300"), COMMISSION);
        new SellTransaction(DTO, new LocalDate("2009-07-30"), new BigDecimal("21"), new BigDecimal("83.8400"), COMMISSION);
        Fund TAO = new Fund("TAO", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("TAO", new LocalDate("2009-08-26"), new BigDecimal("17.2500")));
        quotes.add(new Quote("TAO", new LocalDate("2009-08-12"), new BigDecimal("17.9400")));
        new BuyTransaction(TAO, new LocalDate("2009-07-30"), new BigDecimal("135"), new BigDecimal("18.5600"), COMMISSION);
        new SellTransaction(TAO, new LocalDate("2009-08-28"), new BigDecimal("135"), new BigDecimal("17.0600"), COMMISSION);
        Fund UYM = new Fund("UYM", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("UYM", new LocalDate("2009-12-16"), new BigDecimal("31.9200")));
        quotes.add(new Quote("UYM", new LocalDate("2009-12-02"), new BigDecimal("32.8200")));
        quotes.add(new Quote("UYM", new LocalDate("2009-12-30"), new BigDecimal("33.0900")));
        new BuyTransaction(UYM, new LocalDate("2009-11-30"), new BigDecimal("106"), new BigDecimal("30.8900"), COMMISSION);
        new SellTransaction(UYM, new LocalDate("2009-12-30"), new BigDecimal("106"), new BigDecimal("33.0900"), COMMISSION);
        Fund KOL = new Fund("KOL", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("KOL", new LocalDate("2009-09-09"), new BigDecimal("29.2500")));
        quotes.add(new Quote("KOL", new LocalDate("2009-09-23"), new BigDecimal("31.4000")));
        new BuyTransaction(KOL, new LocalDate("2009-08-28"), new BigDecimal("86"), new BigDecimal("28.0500"), COMMISSION);
        new SellTransaction(KOL, new LocalDate("2009-09-30"), new BigDecimal("86"), new BigDecimal("30.4800"), COMMISSION);
        Fund HAO = new Fund("HAO", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("HAO", new LocalDate("2009-09-09"), new BigDecimal("24.0000")));
        quotes.add(new Quote("HAO", new LocalDate("2009-09-23"), new BigDecimal("24.1100")));
        quotes.add(new Quote("HAO", new LocalDate("2009-08-26"), new BigDecimal("23.2800")));
        quotes.add(new Quote("HAO", new LocalDate("2009-08-12"), new BigDecimal("23.9400")));
        new BuyTransaction(HAO, new LocalDate("2009-07-30"), new BigDecimal("105"), new BigDecimal("23.9100"), COMMISSION);
        new SellTransaction(HAO, new LocalDate("2009-09-30"), new BigDecimal("105"), new BigDecimal("23.5200"), COMMISSION);
        Fund DDP = new Fund("DDP", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("DDP", new LocalDate("2009-03-25"), new BigDecimal("46.5500")));
        quotes.add(new Quote("DDP", new LocalDate("2009-03-11"), new BigDecimal("52.0380")));
        new BuyTransaction(DDP, new LocalDate("2009-02-27"), new BigDecimal("82"), new BigDecimal("50.7600"), COMMISSION);
        new SellTransaction(DDP, new LocalDate("2009-03-30"), new BigDecimal("82"), new BigDecimal("48.7601"), COMMISSION);
        Fund SZO = new Fund("SZO", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("SZO", new LocalDate("2009-06-03"), new BigDecimal("52.9500")));
        quotes.add(new Quote("SZO", new LocalDate("2009-05-20"), new BigDecimal("58.8847")));
        quotes.add(new Quote("SZO", new LocalDate("2009-06-17"), new BigDecimal("49.5000")));
        quotes.add(new Quote("SZO", new LocalDate("2009-07-01"), new BigDecimal("51.2016")));
        quotes.add(new Quote("SZO", new LocalDate("2009-07-29"), new BigDecimal("56.3910")));
        quotes.add(new Quote("SZO", new LocalDate("2009-05-06"), new BigDecimal("65.0799")));
        quotes.add(new Quote("SZO", new LocalDate("2009-07-15"), new BigDecimal("56.4700")));
        quotes.add(new Quote("SZO", new LocalDate("2009-04-22"), new BigDecimal("76.0900")));
        quotes.add(new Quote("SZO", new LocalDate("2009-04-08"), new BigDecimal("70.3700")));
        new BuyTransaction(SZO, new LocalDate("2009-03-30"), new BigDecimal("52"), new BigDecimal("72.5653"), COMMISSION);
        new SellTransaction(SZO, new LocalDate("2009-07-30"), new BigDecimal("52"), new BigDecimal("53.6800"), COMMISSION);
        Fund BDD = new Fund("BDD", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("BDD", new LocalDate("2009-11-18"), new BigDecimal("14.8401")));
        quotes.add(new Quote("BDD", new LocalDate("2009-11-04"), new BigDecimal("13.9700")));
        quotes.add(new Quote("BDD", new LocalDate("2009-10-21"), new BigDecimal("14.2299")));
        quotes.add(new Quote("BDD", new LocalDate("2009-10-07"), new BigDecimal("11.5500")));
        new BuyTransaction(BDD, new LocalDate("2009-09-30"), new BigDecimal("209"), new BigDecimal("11.8730"), COMMISSION);
        new SellTransaction(BDD, new LocalDate("2009-11-30"), new BigDecimal("209"), new BigDecimal("15.3000"), COMMISSION);
        Fund SJF = new Fund("SJF", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("SJF", new LocalDate("2009-01-28"), new BigDecimal("114.6360")));
        quotes.add(new Quote("SJF", new LocalDate("2009-03-25"), new BigDecimal("120.0300")));
        quotes.add(new Quote("SJF", new LocalDate("2009-02-25"), new BigDecimal("145.4400")));
        quotes.add(new Quote("SJF", new LocalDate("2009-02-11"), new BigDecimal("124.8600")));
        quotes.add(new Quote("SJF", new LocalDate("2009-03-11"), new BigDecimal("164.9680")));
        new BuyTransaction(SJF, new LocalDate("2009-01-28"), new BigDecimal("28"), new BigDecimal("114.6360"), COMMISSION);
        new SellTransaction(SJF, new LocalDate("2009-03-30"), new BigDecimal("28"), new BigDecimal("129.2500"), COMMISSION);
        Fund USD = new Fund("USD", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("USD", new LocalDate("2009-09-09"), new BigDecimal("29.4700")));
        quotes.add(new Quote("USD", new LocalDate("2009-09-23"), new BigDecimal("29.4300")));
        new BuyTransaction(USD, new LocalDate("2009-08-28"), new BigDecimal("84"), new BigDecimal("28.7300"), COMMISSION);
        new SellTransaction(USD, new LocalDate("2009-09-30"), new BigDecimal("84"), new BigDecimal("28.4700"), COMMISSION);
        Fund EDC = new Fund("EDC", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("EDC", new LocalDate("2009-11-18"), new BigDecimal("197.7500")));
        quotes.add(new Quote("EDC", new LocalDate("2009-11-04"), new BigDecimal("162.8875")));
        quotes.add(new Quote("EDC", new LocalDate("2009-10-21"), new BigDecimal("192.1375")));
        quotes.add(new Quote("EDC", new LocalDate("2009-12-16"), new BigDecimal("164.2750")));
        quotes.add(new Quote("EDC", new LocalDate("2009-12-02"), new BigDecimal("170.7875")));
        quotes.add(new Quote("EDC", new LocalDate("2009-10-07"), new BigDecimal("173.8750")));
        quotes.add(new Quote("EDC", new LocalDate("2009-12-30"), new BigDecimal("166.4575")));
        new BuyTransaction(EDC, new LocalDate("2009-09-30"), new BigDecimal("14"), new BigDecimal("169.6125"), COMMISSION);
        Fund URE = new Fund("URE", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("URE", new LocalDate("2009-12-30"), new BigDecimal("35.8500")));
        new BuyTransaction(URE, new LocalDate("2009-12-30"), new BigDecimal("97"), new BigDecimal("35.8500"), COMMISSION);
        Fund RSW = new Fund("RSW", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("RSW", new LocalDate("2009-01-28"), new BigDecimal("117.1800")));
        quotes.add(new Quote("RSW", new LocalDate("2009-02-25"), new BigDecimal("147.1900")));
        quotes.add(new Quote("RSW", new LocalDate("2009-02-11"), new BigDecimal("126.5200")));
        new BuyTransaction(RSW, new LocalDate("2009-01-28"), new BigDecimal("28"), new BigDecimal("117.1800"), COMMISSION);
        new SellTransaction(RSW, new LocalDate("2009-02-27"), new BigDecimal("28"), new BigDecimal("158.9700"), COMMISSION);
        Fund TYH = new Fund("TYH", "Test fund", "Category", "Provider", true, "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
        quotes.add(new Quote("TYH", new LocalDate("2009-11-18"), new BigDecimal("41.8725")));
        quotes.add(new Quote("TYH", new LocalDate("2009-11-04"), new BigDecimal("34.3775")));
        quotes.add(new Quote("TYH", new LocalDate("2009-10-21"), new BigDecimal("37.4000")));
        quotes.add(new Quote("TYH", new LocalDate("2009-12-16"), new BigDecimal("35.9625")));
        quotes.add(new Quote("TYH", new LocalDate("2009-12-02"), new BigDecimal("34.4325")));
        quotes.add(new Quote("TYH", new LocalDate("2009-10-07"), new BigDecimal("34.2925")));
        quotes.add(new Quote("TYH", new LocalDate("2009-12-30"), new BigDecimal("40.7250")));
        new BuyTransaction(TYH, new LocalDate("2009-09-30"), new BigDecimal("72"), new BigDecimal("34.5050"), COMMISSION);

//        Portfolio portfolio = new SimplePortfolio();
//        for (Transaction transaction : transactions) {
//            portfolio.add(transaction);
//        }

    }
}