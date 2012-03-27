package com.mns.mojoinvest.server.engine.result;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.Position;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.server.engine.transaction.Transaction;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import com.mns.mojoinvest.shared.dto.DataTableDto;
import com.mns.mojoinvest.shared.dto.StrategyResult;
import com.mns.mojoinvest.shared.dto.TransactionDto;
import org.joda.time.LocalDate;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.logging.Logger;

public class StrategyResultBuilder {

    private static final Logger log = Logger.getLogger(StrategyResultBuilder.class.getName());

    private final QuoteDao quoteDao;

    @Inject
    public StrategyResultBuilder(QuoteDao quoteDao) {
        this.quoteDao = quoteDao;
    }

    public StrategyResult build(Portfolio portfolio, LocalDate fromDate, LocalDate toDate) {

        log.info("Building transaction history");
//        List<TransactionDto> transactionDtos = new ArrayList<TransactionDto>(0);
        List<TransactionDto> transactionDtos = getTransactionHistory(portfolio);
        //TODO: Make this call when user navigates to transaction tab? getTransactionHistory(portfolio);

        //TODO: The date series should not contain dates outside of the range
        LocalDate fromLocalDate = new LocalDate(fromDate);
        LocalDate toLocalDate = new LocalDate(toDate);
        List<LocalDate> dates;
        if (toLocalDate.isAfter(fromLocalDate.plusYears(2))) {
            dates = TradingDayUtils.getMonthlySeries(fromLocalDate, toLocalDate, 1, true);
        } else {
            dates = TradingDayUtils.getWeeklySeries(fromLocalDate, toLocalDate, 2, true);
        }

        log.info("Caching quotes for portfolio calculations");
        Collection<Quote> quotes = cacheQuotesForPortfolio(portfolio, dates);
//        log.info(logQuotes(quotes));
        log.info("Calculating portfolio market values for date series");
        List<BigDecimal> marketValues = portfolio.marketValue(dates);

        log.info("Creating result set dto");
        DataTableDto dataTableDto = createDataTableDto(marketValues, dates);

        log.info("Returning strategy results");
        return new StrategyResult(dataTableDto, new ArrayList<TransactionDto>(transactionDtos));
    }

    private String logQuotes(Collection<Quote> quotes) {
        StringWriter stringWriter = new StringWriter();
        CSVWriter writer = new CSVWriter(stringWriter);
        for (Quote quote : quotes) {
            writer.writeNext(quote.toStrArr());
        }
        return stringWriter.toString();
    }

    private Collection<Quote> cacheQuotesForPortfolio(Portfolio portfolio, List<LocalDate> dates) {
        List<Key<Quote>> keys = new ArrayList<Key<Quote>>();
        for (Position position : portfolio.getPositions()) {
            keys.addAll(quoteDao.getKeys(position.getFund().getSymbol(), position.getActiveDates(dates)));
        }
        log.info("Found " + keys.size() + " quote keys. Starting load...");

        return quoteDao.get(keys);
    }

    private DataTableDto createDataTableDto(List<BigDecimal> marketValues, List<LocalDate> dates) {

        DataTableDto dto = new DataTableDto();
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.DATE, "Date", "date"));
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "Portfolio value", "portfolio"));
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "S&P 500", "SPY"));
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "Dow Jones Industrial Average", "DIA"));
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "Nasdaq-100 Index", "QQQ"));

        List<String> funds = Arrays.asList("SPY", "DIA", "QQQ");
        Collection<Quote> quotes = quoteDao.get(funds, dates);
        Map<String, Map<LocalDate, BigDecimal>> quoteMap = new HashMap<String, Map<LocalDate, BigDecimal>>();
        for (Quote quote : quotes) {
            if (!quoteMap.containsKey(quote.getSymbol())) {
                quoteMap.put(quote.getSymbol(), new HashMap<LocalDate, BigDecimal>());
            }
            quoteMap.get(quote.getSymbol()).put(quote.getDate(), quote.getAdjClose());
        }

        for (int i = 0; i < marketValues.size(); i++) {
            BigDecimal spy = percentageChange(quoteMap.get("SPY").get(dates.get(0)),
                    quoteMap.get("SPY").get(dates.get(i)));
            BigDecimal dia = percentageChange(quoteMap.get("DIA").get(dates.get(0)),
                    quoteMap.get("DIA").get(dates.get(i)));
            BigDecimal qqq = percentageChange(quoteMap.get("QQQ").get(dates.get(0)),
                    quoteMap.get("QQQ").get(dates.get(i)));
            dto.addRow(new DataTableDto.DateValue(dates.get(i).toDateMidnight().toDate()),
                    new DataTableDto.DoubleValue(marketValues.get(i).doubleValue())
                    , new DataTableDto.DoubleValue((spy.doubleValue() + 1) * 10000)
                    , new DataTableDto.DoubleValue((dia.doubleValue() + 1) * 10000)
                    , new DataTableDto.DoubleValue((qqq.doubleValue() + 1) * 10000)
            );
        }

        return dto;
    }

    private List<TransactionDto> getTransactionHistory(Portfolio portfolio) {
        List<Transaction> transactions = new ArrayList<Transaction>();
        for (Position position : portfolio.getPositions()) {
            transactions.addAll(position.getTransactions());
        }
        sortByDate(transactions);

        List<TransactionDto> transactionDtos = new ArrayList<TransactionDto>();
        for (Transaction transaction : transactions) {
            if (transaction instanceof BuyTransaction) {
                transactionDtos.add(new TransactionDto("Buy", transaction.getFund(), transaction.getFund() + "get desc",
                        transaction.getDate().toDateMidnight().toDate(), transaction.getQuantity().doubleValue(),
                        transaction.getPrice().doubleValue(), transaction.getCommission().doubleValue()));
            } else if (transaction instanceof SellTransaction) {
                transactionDtos.add(new TransactionDto("Sell", transaction.getFund(), transaction.getFund() + "get desc",
                        transaction.getDate().toDateMidnight().toDate(), transaction.getQuantity().doubleValue(),
                        transaction.getPrice().doubleValue(), transaction.getCommission().doubleValue()));
            }
        }
        return transactionDtos;
    }

    public static void sortByDate(List<Transaction> transactions) {
        Collections.sort(transactions, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction q1, Transaction q2) {
                return q2.getDate().compareTo(q1.getDate());
            }
        });
    }


    private static BigDecimal percentageChange(BigDecimal from, BigDecimal to) {
        BigDecimal change = to.subtract(from);
        return change.divide(from, MathContext.DECIMAL32);
    }
}
