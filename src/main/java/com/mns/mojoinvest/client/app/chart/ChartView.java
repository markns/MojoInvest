package com.mns.mojoinvest.client.app.chart;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.gwtplatform.mvp.client.ViewImpl;

public class ChartView extends ViewImpl
        implements ChartPresenter.MyView {

    interface ChartViewUiBinder extends UiBinder<Widget, ChartView> {
    }

    private static ChartViewUiBinder uiBinder = GWT.create(ChartViewUiBinder.class);

    @UiField
    HTMLPanel container;

    private LineChart lineChart;

    public final Widget widget;

    public ChartView() {
        widget = uiBinder.createAndBindUi(this);
        VisualizationUtils.loadVisualizationApi(new Runnable() {
            @Override
            public void run() {
            }
        }, LineChart.PACKAGE);
    }

    @Override
    public void createChart(DataTable dataTable, Options options) {
        lineChart = new LineChart(dataTable, options);
        container.clear();
        container.add(lineChart);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }
}
