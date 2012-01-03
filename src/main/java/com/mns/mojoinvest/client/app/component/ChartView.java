package com.mns.mojoinvest.client.app.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ChartView extends Composite
        implements ChartPresenter.MyView {

    interface ChartViewUiBinder extends UiBinder<Widget, ChartView> {
    }

    private static ChartViewUiBinder uiBinder = GWT.create(ChartViewUiBinder.class);

    @UiField
    HTMLPanel container;

    public ChartView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

//    @Override
//    public Widget asWidget() {
//        return widget;
//    }

    @Override
    public void addToSlot(Object slot, Widget content) {
    }

    @Override
    public void removeFromSlot(Object slot, Widget content) {
    }

    @Override
    public void setInSlot(Object slot, Widget content) {
    }
}
