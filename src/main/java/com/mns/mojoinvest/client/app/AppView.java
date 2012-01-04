package com.mns.mojoinvest.client.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.mns.mojoinvest.client.util.UiUtils;

public class AppView extends ViewWithUiHandlers<AppUiHandlers>
        implements AppPresenter.MyView {

    interface AppViewUiBinder extends UiBinder<Widget, AppView> {
    }

    private static AppViewUiBinder uiBinder = GWT.create(AppViewUiBinder.class);

    public final Widget widget;

    @UiField
    FlowPanel trades;

    @UiField
    FlowPanel chart;

    @UiField
    FlowPanel params;

    public AppView() {
        widget = uiBinder.createAndBindUi(this);


    }


    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {
        if (slot == AppPresenter.SLOT_trades) {
            UiUtils.setContent(trades, content);
        } else if (slot == AppPresenter.SLOT_params) {
            UiUtils.setContent(params, content);
        } else if (slot == AppPresenter.SLOT_chart) {
            UiUtils.setContent(chart, content);
        } else {
            super.setInSlot(slot, content);
        }
    }


//    @Override
//    public void setChartData(DataTable dataTable, OptionsDto optionsDto) {
//        lineChart = new LineChart(dataTable, createOptions(optionsDto));
//        chartContainer.clear();
//        chartContainer.add(lineChart);
//    }

//    private Options createOptions(OptionsDto optionsDto) {
//        Options options = Options.create();
//        options.setTitle(optionsDto.getTitle());
//        options.setWidth(800);
//        options.setHeight(400);
//        return options;
//    }

//    @Override
//    public void setDefaultValues() {
//    }
//
//    @Override
//    public void resetAndFocus() {
//        setDefaultValues();
//    }


//    @UiHandler("getPerformance")
//    void onCreatePerformanceClicked(ClickEvent event) {
//        getUiHandlers().getPerformance(symbol.getValue());
//    }

}
