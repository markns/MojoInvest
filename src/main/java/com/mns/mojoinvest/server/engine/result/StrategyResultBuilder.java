package com.mns.mojoinvest.server.engine.result;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
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

    private final FundDao fundDao;
    private final QuoteDao quoteDao;

    @Inject
    public StrategyResultBuilder(FundDao fundDao, QuoteDao quoteDao) {
        this.fundDao = fundDao;
        this.quoteDao = quoteDao;
    }

    public StrategyResult build(Portfolio portfolio, LocalDate fromDate, LocalDate toDate) {

        log.info("Building transaction history");
        List<TransactionDto> transactionDtos = getTransactionHistory(portfolio);

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
        return quoteDao.get(portfolio.getFunds(), dates);
    }


    private DataTableDto createDataTableDto(List<BigDecimal> marketValues, List<LocalDate> dates) {

        DataTableDto dto = new DataTableDto();
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.DATE, "Date", "date"));
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "Portfolio value", "portfolio"));
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "SPDR S&P 500", "SPY"));

        Collection<Fund> funds = fundDao.get(Arrays.asList("SPY"));
        Collection<Quote> quotes = quoteDao.get(funds, dates);

        Map<String, Map<LocalDate, BigDecimal>> quoteMap = new HashMap<String, Map<LocalDate, BigDecimal>>();
        for (Quote quote : quotes) {
            if (!quoteMap.containsKey(quote.getSymbol())) {
                quoteMap.put(quote.getSymbol(), new HashMap<LocalDate, BigDecimal>());
            }
            quoteMap.get(quote.getSymbol()).put(quote.getDate(), quote.getClose());
        }

        for (int i = 0; i < marketValues.size(); i++) {
            BigDecimal isfchange = percentageChange(quoteMap.get("SPY").get(dates.get(0)), quoteMap.get("SPY").get(dates.get(i)));
            dto.addRow(new DataTableDto.DateValue(dates.get(i).toDateMidnight().toDate()),
                    new DataTableDto.DoubleValue(marketValues.get(i).doubleValue()),
                    new DataTableDto.DoubleValue((isfchange.doubleValue() + 1) * 10000)
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
                //TODO: Shouldn't have to create this DTO
                transactionDtos.add(new TransactionDto("Buy", transaction.getFund().getSymbol(), transaction.getFund().getName(),
                        transaction.getDate().toDateMidnight().toDate(), transaction.getQuantity().doubleValue(),
                        transaction.getPrice().doubleValue(), transaction.getCommission().doubleValue()));
            } else if (transaction instanceof SellTransaction) {
                transactionDtos.add(new TransactionDto("Sell", transaction.getFund().getSymbol(), transaction.getFund().getName(),
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
