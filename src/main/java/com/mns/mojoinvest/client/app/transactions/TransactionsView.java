package com.mns.mojoinvest.client.app.transactions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

public class TransactionsView extends ViewImpl
        implements TransactionsPresenter.MyView {

    interface TransactionsViewUiBinder extends UiBinder<Widget, TransactionsView> {
    }

    private static TransactionsViewUiBinder uiBinder = GWT.create(TransactionsViewUiBinder.class);

    @UiField
    HTMLPanel container;
    @UiField
    CellTable table;

    public final Widget widget;

    public TransactionsView() {
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }
}
