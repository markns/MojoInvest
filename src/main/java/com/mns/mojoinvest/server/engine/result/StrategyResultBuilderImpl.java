package com.mns.mojoinvest.server.engine.result;

import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.portfolio.Lot;
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

        List<LocalDate> dates = TradingDayUtils.getWeeklySeries(new LocalDate(fromDate), new LocalDate(toDate), 1, true);
        for (LocalDate date : dates) {
            BigDecimal marketValue = portfolio.marketValue(date);
            log.info(date + " " + marketValue);
            dto.addRow(new DataTableDto.DateValue(date.toDateMidnight().toDate()),
                    new DataTableDto.DoubleValue(portfolio.marketValue(date).doubleValue()));
        }

        return dto;
    }

    private List<TransactionDto> getTransactionHistory(Portfolio portfolio) {
        List<Transaction> transactions = new ArrayList<Transaction>();
        for (Position position : portfolio.getPositions()) {
            for (Lot lot : position.getLots()) {
                transactions.add(lot.getOpeningTransaction());
                transactions.addAll(lot.getClosingTransactions());
            }
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
}
