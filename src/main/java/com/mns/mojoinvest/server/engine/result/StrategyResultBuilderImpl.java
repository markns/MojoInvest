package com.mns.mojoinvest.server.engine.result;

import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.logging.Logger;

public class StrategyResultBuilderImpl implements StrategyResultBuilder {

    private static final Logger log = Logger.getLogger(StrategyResultBuilderImpl.class.getName());

    private final FundDao fundDao;
    private final QuoteDao quoteDao;
    private Portfolio portfolio;

    @Inject
    public StrategyResultBuilderImpl(FundDao fundDao, QuoteDao quoteDao,
                                     @Assisted Portfolio portfolio) {
        this.fundDao = fundDao;
        this.quoteDao = quoteDao;
        this.portfolio = portfolio;
    }

    @Override
    public StrategyResult build() {
        List<TransactionDto> transactionDtos = getTransactionHistory(portfolio);
        DataTableDto dataTableDto = createDataTableDto(portfolio, transactionDtos.get(0).getDate(),
                transactionDtos.get(transactionDtos.size() - 1).getDate());

        return new StrategyResult(dataTableDto, transactionDtos);
    }


    //TODO: Tidy up the handling of dates here
    private DataTableDto createDataTableDto(Portfolio portfolio, Date fromDate, Date toDate) {

        DataTableDto dto = new DataTableDto();
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.DATE, "Date", "date"));
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "Portfolio value", "portfolio"));
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "iShares S&P 500", "s&p500"));
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "iShares FTSE 100", "ftse100"));
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "iShares FTSE China 25", "ftse100"));

        List<LocalDate> dates = TradingDayUtils.getWeeklySeries(new LocalDate(fromDate), new LocalDate(toDate), 2, true);

        Collection<Fund> funds = fundDao.get(Arrays.asList("ISF", "IUSA", "FXC"));
        Collection<Quote> quotes = quoteDao.get(funds, dates);
        Map<String, Map<LocalDate, BigDecimal>> quoteMap = new HashMap<String, Map<LocalDate, BigDecimal>>();
        for (Quote quote : quotes) {
            if (!quoteMap.containsKey(quote.getSymbol())) {
                quoteMap.put(quote.getSymbol(), new HashMap<LocalDate, BigDecimal>());
            }
            quoteMap.get(quote.getSymbol()).put(quote.getDate(), quote.getClose());
        }

        for (LocalDate date : dates) {
            BigDecimal marketValue = portfolio.marketValue(date);

            BigDecimal isfchange = percentageChange(quoteMap.get("ISF").get(dates.get(0)), quoteMap.get("ISF").get(date));
            BigDecimal iusachange = percentageChange(quoteMap.get("IUSA").get(dates.get(0)), quoteMap.get("IUSA").get(date));
            BigDecimal fxcchange = percentageChange(quoteMap.get("FXC").get(dates.get(0)), quoteMap.get("FXC").get(date));


            log.info(date + " " + marketValue + " " + portfolio.getActiveFunds(date));
            dto.addRow(new DataTableDto.DateValue(date.toDateMidnight().toDate()),
                    new DataTableDto.DoubleValue(portfolio.marketValue(date).doubleValue()),
                    new DataTableDto.DoubleValue((isfchange.doubleValue() + 1) * 10000),
                    new DataTableDto.DoubleValue((iusachange.doubleValue() + 1) * 10000),
                    new DataTableDto.DoubleValue((fxcchange.doubleValue() + 1) * 10000)

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
                return q1.getDate().compareTo(q2.getDate());
            }
        });
    }


    private static BigDecimal percentageChange(BigDecimal from, BigDecimal to) {
        BigDecimal change = to.subtract(from);
        return change.divide(from, MathContext.DECIMAL32);
    }
}
