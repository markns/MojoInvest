package com.mns.mojoinvest.server.handler;

import com.google.gwt.visualization.client.AbstractDataTable;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.mojoinvest.shared.dispatch.GetFundPerformanceAction;
import com.mns.mojoinvest.shared.dispatch.GetFundPerformanceResult;
import com.mns.mojoinvest.shared.dto.DataTableDto;
import org.joda.time.LocalDate;

public class GetFundPerformanceHandler implements
        ActionHandler<GetFundPerformanceAction, GetFundPerformanceResult> {

    @Override
    public GetFundPerformanceResult execute(GetFundPerformanceAction action, ExecutionContext context) throws ActionException {
        return new GetFundPerformanceResult("", createTable());
    }

    @Override
    public Class<GetFundPerformanceAction> getActionType() {
        return GetFundPerformanceAction.class;
    }

    @Override
    public void undo(GetFundPerformanceAction action, GetFundPerformanceResult result, ExecutionContext context) throws ActionException {
    }

    private DataTableDto createTable() {

        DataTableDto dto = new DataTableDto();
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.DATE, "Date", "date"));
        dto.addColumn(new DataTableDto.Column(AbstractDataTable.ColumnType.NUMBER, "Close", "close"));

        dto.addRow(new DataTableDto.DateValue(new LocalDate("2011-01-01").toDateMidnight().toDate()), new DataTableDto.IntegerValue(102));
        dto.addRow(new DataTableDto.DateValue(new LocalDate("2011-02-01").toDateMidnight().toDate()), new DataTableDto.IntegerValue(132));
        dto.addRow(new DataTableDto.DateValue(new LocalDate("2011-03-01").toDateMidnight().toDate()), new DataTableDto.IntegerValue(143));
        dto.addRow(new DataTableDto.DateValue(new LocalDate("2011-04-01").toDateMidnight().toDate()), new DataTableDto.IntegerValue(132));
        dto.addRow(new DataTableDto.DateValue(new LocalDate("2011-05-01").toDateMidnight().toDate()), new DataTableDto.IntegerValue(154));
        dto.addRow(new DataTableDto.DateValue(new LocalDate("2011-06-01").toDateMidnight().toDate()), new DataTableDto.IntegerValue(143));
        dto.addRow(new DataTableDto.DateValue(new LocalDate("2011-07-01").toDateMidnight().toDate()), new DataTableDto.IntegerValue(165));
        dto.addRow(new DataTableDto.DateValue(new LocalDate("2011-08-01").toDateMidnight().toDate()), new DataTableDto.IntegerValue(123));
        dto.addRow(new DataTableDto.DateValue(new LocalDate("2011-09-01").toDateMidnight().toDate()), new DataTableDto.IntegerValue(176));

        return dto;
    }

}
