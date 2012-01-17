package com.mns.mojoinvest.server.handler;

import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDaoImpl;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import com.mns.mojoinvest.shared.dispatch.GetFundPerformanceAction;
import com.mns.mojoinvest.shared.dispatch.GetFundPerformanceResult;
import com.mns.mojoinvest.shared.dto.DataTableDto;
import com.mns.mojoinvest.shared.dto.OptionsDto;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;

public class GetFundPerformanceHandler implements
        ActionHandler<GetFundPerformanceAction, GetFundPerformanceResult> {

    private final QuoteDaoImpl quoteDao;
    private final FundDao fundDao;

    @Inject
    public GetFundPerformanceHandler(QuoteDaoImpl quoteDao, FundDao fundDao) {
        this.quoteDao = quoteDao;
        this.fundDao = fundDao;
    }


    @Override
    public GetFundPerformanceResult execute(GetFundPerformanceAction action, ExecutionContext context) throws ActionException {
        Fund fund = fundDao.get(action.getSymbol());

        LocalDate inceptionDate = fund.getInceptionDate();
        LocalDate today = new LocalDate();

        List<LocalDate> dates;
        if (today.isAfter(inceptionDate.plusYears(2))) {
            dates = TradingDayUtils.getMonthlySeries(inceptionDate, today, 1, true);
        } else if (today.isAfter(inceptionDate.plusMonths(6))) {
            dates = TradingDayUtils.getWeeklySeries(inceptionDate, today, 1, true);
        } else {
            dates = TradingDayUtils.getDailySeries(inceptionDate, today, true);
        }

        Collection<Quote> quotes = quoteDao.get(fund, dates);
        return new GetFundPerformanceResult("", createDataTableDto(quotes), new OptionsDto(fund.getName() + " (" + fund.getSymbol() + ")"));
    }

    @Override
    public Class<GetFundPerformanceAction> getActionType() {
        return GetFundPerformanceAction.class;
    }

    @Override
    public void undo(GetFundPerformanceAction action, GetFundPerformanceResult result, ExecutionContext context) throws ActionException {
    }

    private DataTableDto createDataTableDto(Collection<Quote> quotes) {

        DataTableDto dto = new DataTableDto();
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.DATE, "Date", "date"));
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "Close", "close"));

        for (Quote quote : quotes) {
            dto.addRow(new DataTableDto.DateValue(quote.getDate().toDateMidnight().toDate()),
                    new DataTableDto.DoubleValue(quote.getClose().doubleValue()));

        }
        return dto;
    }

}
