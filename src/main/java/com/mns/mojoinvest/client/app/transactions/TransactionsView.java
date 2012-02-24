package com.mns.mojoinvest.client.app.transactions;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.gwtplatform.mvp.client.ViewImpl;
import com.mns.mojoinvest.client.resources.Resources;
import com.mns.mojoinvest.shared.dto.TransactionDto;

import java.util.Date;
import java.util.List;

public class TransactionsView extends ViewImpl
        implements TransactionsPresenter.MyView {

    interface TransactionsViewUiBinder extends UiBinder<Widget, TransactionsView> {
    }

    private static TransactionsViewUiBinder uiBinder = GWT.create(TransactionsViewUiBinder.class);

    @UiField
    HTMLPanel container;

    @UiField(provided = true)
    CellTable<TransactionDto> table;

    @UiField
    SimplePager pager;

    public final Widget widget;

    public TransactionsView() {

        CellTable.Resources resources = GWT.create(Resources.TableResources.class);
        table = new CellTable<TransactionDto>(10, resources);

        widget = uiBinder.createAndBindUi(this);

        Column<TransactionDto, SafeHtml> fundNameColumn = new Column<TransactionDto, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(TransactionDto transaction) {
                String val = transaction.getFundName().substring(0, 20) + "...";
                return new SafeHtmlBuilder().appendHtmlConstant("<span title='" +
                        new SafeHtmlBuilder().appendEscaped(transaction.getFundName()).toSafeHtml().asString() + "'>" + val + "</span>").toSafeHtml();
            }
        };
        table.addColumn(fundNameColumn, "Name");
        table.setColumnWidth(fundNameColumn, "30em");

        Column<TransactionDto, String> symbolColumn = new TextColumn<TransactionDto>() {
            @Override
            public String getValue(TransactionDto transaction) {
                return transaction.getSymbol();
            }
        };
        table.addColumn(symbolColumn, "Symbol");
//        table.setColumnWidth(symbolColumn, "25ex");

        Column<TransactionDto, String> buySellColumn = new TextColumn<TransactionDto>() {
            @Override
            public String getValue(TransactionDto transaction) {
                return transaction.getBuySell();
            }
        };
        table.addColumn(buySellColumn, "Type");
//        table.setColumnWidth(buySellColumn, "25ex");

        DateCell dateCell = new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM));
        Column<TransactionDto, Date> dateColumn = new Column<TransactionDto, Date>(dateCell) {
            @Override
            public Date getValue(TransactionDto transaction) {
                return transaction.getDate();
            }
        };
        table.addColumn(dateColumn, "Date");
        table.setColumnWidth(dateColumn, "25em");


        NumberCell sharesCell = new NumberCell(); //TODO: Formatting
        Column<TransactionDto, Number> unitsColumn = new Column<TransactionDto, Number>(sharesCell) {
            @Override
            public Number getValue(TransactionDto transaction) {
                return transaction.getUnits();
            }
        };
        table.addColumn(unitsColumn, "Shares");

        NumberCell priceCell = new NumberCell(NumberFormat.getCurrencyFormat());
        Column<TransactionDto, Number> priceColumn = new Column<TransactionDto, Number>(priceCell) {
            @Override
            public Number getValue(TransactionDto transaction) {
                return transaction.getPrice();
            }
        };
        table.addColumn(priceColumn, "Price");

        NumberCell cashValueCell = new NumberCell(NumberFormat.getCurrencyFormat());
        Column<TransactionDto, Number> cashValueColumn = new Column<TransactionDto, Number>(cashValueCell) {
            @Override
            public Number getValue(TransactionDto transaction) {
                return Double.NaN;
            }
        };
        table.addColumn(cashValueColumn, "Cash value");
        table.setColumnWidth(dateColumn, "20em");


//        NumberCell commissionCell = new NumberCell(NumberFormat.getCurrencyFormat());
//        Column<TransactionDto, Number> commissionColumn = new Column<TransactionDto, Number>(commissionCell) {
//            @Override
//            public Number getValue(TransactionDto transaction) {
//                return transaction.getCommission();
//            }
//        };
//        table.addColumn(commissionColumn, "Commission");


        // Connect the table to the data provider.
        dataProvider.addDataDisplay(table);


//        table.setRowCount(numRows, false);
        table.setSelectionModel(new NoSelectionModel<TransactionDto>());
//        table.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

    }

    // Create a data provider.
    ListDataProvider<TransactionDto> dataProvider = new ListDataProvider<TransactionDto>();


//    private final SingleSelectionModel<TransactionDto> selectionModel = new SingleSelectionModel<TransactionDto>();


    @Override
    public void refreshTransactions(List<TransactionDto> transactions) {

        // Add the data to the data provider, which automatically pushes it to the
        // widget.
        List<TransactionDto> list = dataProvider.getList();
        list.clear();
        for (TransactionDto transaction : transactions) {
            list.add(transaction);
        }

    }

    @Override
    public Widget asWidget() {
        return widget;
    }
}
