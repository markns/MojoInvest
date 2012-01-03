package com.mns.mojoinvest.client.app.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

public class ChartView extends ViewImpl
        implements ChartPresenter.MyView {

    interface ChartViewUiBinder extends UiBinder<Widget, ChartView> {
    }

    private static ChartViewUiBinder uiBinder = GWT.create(ChartViewUiBinder.class);

    @UiField
    HTMLPanel container;

    public final Widget widget;

    public ChartView() {
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }
}
