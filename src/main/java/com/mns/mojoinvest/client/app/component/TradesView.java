package com.mns.mojoinvest.client.app.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

public class TradesView extends ViewImpl
        implements TradesPresenter.MyView {

    interface BacktestParamsViewUiBinder extends UiBinder<Widget, TradesView> {
    }

    private static BacktestParamsViewUiBinder uiBinder = GWT.create(BacktestParamsViewUiBinder.class);

    @UiField
    HTMLPanel container;
    @UiField
    CellTable table;

    public final Widget widget;

    public TradesView() {
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }
}
