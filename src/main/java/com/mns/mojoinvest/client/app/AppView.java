package com.mns.mojoinvest.client.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.mns.mojoinvest.shared.dto.OptionsDto;

public class AppView extends ViewWithUiHandlers<AppUiHandlers>
        implements AppPresenter.MyView {

    interface AppViewUiBinder extends UiBinder<Widget, AppView> {
    }

    private static AppViewUiBinder uiBinder = GWT.create(AppViewUiBinder.class);

    interface PerformancesResources extends CellList.Resources {
        @Source(value = {CellList.Style.DEFAULT_CSS, "../resources/cell.css"})
        CellList.Style cellListStyle();
    }

    public final Widget widget;

    @UiField
    HTMLPanel container;

    @UiField
    TextBox symbol;

    @UiField
    Button getPerformance;

    @UiField
    SimplePanel chartContainer;

    private LineChart lineChart;

    public AppView() {
        widget = uiBinder.createAndBindUi(this);

        VisualizationUtils.loadVisualizationApi(new Runnable() {
            @Override
            public void run() {
//                lineChart = new LineChart(createTable(), createOptions());
//                chartContainer.clear();
//                chartContainer.add(lineChart);

            }
        }, LineChart.PACKAGE);

    }


    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setDefaultValues() {
    }

    @Override
    public void setChartData(DataTable dataTable, OptionsDto optionsDto) {
        lineChart = new LineChart(dataTable, createOptions(optionsDto));
        chartContainer.clear();
        chartContainer.add(lineChart);
    }

    private AbstractDataTable createTable() {
        DataTable data = DataTable.create();
        data.addColumn(AbstractDataTable.ColumnType.STRING, "Task");
        data.addColumn(AbstractDataTable.ColumnType.NUMBER, "Hours per Day");
        data.addRows(2);
        data.setValue(0, 0, "Work");
        data.setValue(0, 1, 14);
        data.setValue(1, 0, "Sleep");
        data.setValue(1, 1, 10);
        return data;
    }


    private Options createOptions(OptionsDto optionsDto) {
        Options options = Options.create();
        options.setTitle(optionsDto.getTitle());
        options.setWidth(800);
        options.setHeight(400);
        options.setTitle("Fund Performance");
        return options;
    }

    public void resetAndFocus() {
        setDefaultValues();
    }


    @UiHandler("getPerformance")
    void onCreatePerformanceClicked(ClickEvent event) {
        getUiHandlers().getPerformance(symbol.getValue());
    }

}
