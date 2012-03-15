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

        return new StrategyResult(dataTableDto, new ArrayList<TransactionDto>(transactionDtos));
    }


    //TODO: Tidy up the handling of dates here
    private DataTableDto createDataTableDto(Portfolio portfolio, Date fromDate, Date toDate) {

        DataTableDto dto = new DataTableDto();
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.DATE, "Date", "date"));
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "Portfolio value", "portfolio"));
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "SPDR S&P 500", "SPY"));
//        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "iShares FTSE 100", "ftse100"));
//        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "iShares FTSE China 25", "ftse100"));
//        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "iShares FTSE BRIC 50", "bric100"));

        List<LocalDate> dates = TradingDayUtils.getWeeklySeries(new LocalDate(fromDate), new LocalDate(toDate), 2, true);

        Collection<Fund> funds = fundDao.get(Arrays.asList("SPY"));
        Collection<Quote> quotes = quoteDao.get(funds, dates);
        Map<String, Map<LocalDate, BigDecimal>> quoteMap = new HashMap<String, Map<LocalDate, BigDecimal>>();
        for (Quote quote : quotes) {
            if (!quoteMap.containsKey(quote.getSymbol())) {
                quoteMap.put(quote.getSymbol(), new HashMap<LocalDate, BigDecimal>());
            }
            quoteMap.get(quote.getSymbol()).put(quote.getDate(), quote.getClose());
        }

        List<BigDecimal> marketValues = portfolio.marketValue(new TreeSet<LocalDate>(dates));

        for (int i = 0; i < marketValues.size(); i++) {

            BigDecimal isfchange = percentageChange(quoteMap.get("SPY").get(dates.get(0)), quoteMap.get("SPY").get(dates.get(i)));
//            log.info(dates.get(i) + " " + marketValues.get(i) + " " + portfolio.getActiveFunds(dates.get(i)));
            dto.addRow(new DataTableDto.DateValue(dates.get(i).toDateMidnight().toDate()),
                    new DataTableDto.DoubleValue(marketValues.get(i).doubleValue()),
                    new DataTableDto.DoubleValue((isfchange.doubleValue() + 1) * 10000)
//                    new DataTableDto.DoubleValue((iusachange.doubleValue() + 1) * 10000)
//                    new DataTableDto.DoubleValue((fxcchange.doubleValue() + 1) * 10000)
//                    ,new DataTableDto.DoubleValue((bricchange.doubleValue() + 1) * 10000)

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
