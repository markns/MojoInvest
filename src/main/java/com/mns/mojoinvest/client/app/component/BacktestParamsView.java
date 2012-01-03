package com.mns.mojoinvest.client.app.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

public class BacktestParamsView extends ViewImpl
        implements BacktestParamsPresenter.MyView
//        , Editor<BackTestParams>
{

    interface BacktestParamsViewUiBinder extends UiBinder<Widget, BacktestParamsView> { }

    private static BacktestParamsViewUiBinder uiBinder = GWT.create(BacktestParamsViewUiBinder.class);

    @UiField
    HTMLPanel container;

	public final Widget widget;

    public BacktestParamsView() {
         widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }
}
