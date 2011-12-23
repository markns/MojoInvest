package com.mns.mojoinvest.client.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.mns.mojoinvest.shared.Constants;

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
    DateBox date;

    // @UiField
    // TextBox show;
    @UiField(provided = true)
    SuggestBox show;

    // @UiField
    // TextBox location;
    @UiField(provided = true)
    SuggestBox location;

    @UiField
    Button updatePerformance;
    @UiField
    Button deletePerformance;
    @UiField
    HTMLPanel container;
    @UiField
    InlineHyperlink guide;

    @UiField
    SimplePanel chartContainer;


    MultiWordSuggestOracle showSO, locationSO;


    // Google Visualization scatter plot and associated options.
    private LineChart lineChart;

    public AppView() {
        showSO = new MultiWordSuggestOracle();
        show = new SuggestBox(showSO);
        locationSO = new MultiWordSuggestOracle();
        location = new SuggestBox(locationSO);

        widget = uiBinder.createAndBindUi(this);

        VisualizationUtils.loadVisualizationApi(new Runnable() {
            @Override
            public void run() {
                lineChart = new LineChart(createTable(), createOptions());
                chartContainer.clear();
                chartContainer.add(lineChart);

            }
        }, LineChart.PACKAGE);


        // fallback for browsers which don't support placeholder attribute
        // null == date.getElement().getAttribute("placeholder")
        // if (placeholderSupport().equals("no")) {
        // dateLbl.setVisible(true);
        // showLbl.setVisible(true);
        // locationLbl.setVisible(true);
        // }

        date.setFormat(new DateBox.DefaultFormat(DateTimeFormat
                .getFormat(Constants.defaultDateFormat)));
        // workaround for
        // http://code.google.com/p/google-web-toolkit/issues/detail?id=5295
        // date.getElement().setAttribute("placeholder", "Date and time");
        // show.getElement().setAttribute("placeholder", "Show name");
        // location.getElement().setAttribute("placeholder", "Location");
        date.getElement().setAttribute("required", "");
        show.getElement().setAttribute("required", "");
        show.getElement().setAttribute("required", "");

//		performancesCL.setVisibleRange(Constants.visibleRangeStart,
//				Constants.visibleRangeLength);
//		performancesAsyncAdapter = new PerformancesAsyncAdapter();
//		performancesAsyncAdapter.addDataDisplay(performancesCL);

    }


    // workaround for
    // http://code.google.com/p/google-web-toolkit/issues/detail?id=5541
    // private static native String placeholderSupport() /*-{
    // var i = document.createElement('input');
    // if ('placeholder' in i) {
    // return "yes";
    // } else {
    // return "no";
    // }
    // }-*/;

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setDefaultValues() {
        date.setValue(null);
        show.setValue("");
        location.setValue("");
    }

    @Override
    public void setChartData(DataTable dataTable) {
        new LineChart(dataTable, createOptions());
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


    private Options createOptions() {
        Options options = Options.create();
        options.setWidth(400);
        options.setHeight(240);
        options.setTitle("My Daily Activities");
        return options;
    }

    public void resetAndFocus() {
        setDefaultValues();
    }
//
//	@UiFactory
//    CellList<Performance> createPerformanceCL() {
//		PerformanceCell performanceCell = new PerformanceCell();
//		SafeHtmlBuilder sb = new SafeHtmlBuilder();
//		CellList<Performance> cl = new CellList<Performance>(
//				performanceCell,
//				GWT.<PerformancesResources> create(PerformancesResources.class),
//				Performance.KEY_PROVIDER);
//		cl.setEmptyListMessage(sb.appendHtmlConstant(
//				"No performances created yet").toSafeHtml());
//		setSelectionModel(cl);
//
//		return cl;
//	}
//
//	@Override
//	public void loadPerformanceData(Integer start, Integer length,
//			List<Performance> performances) {
//		setIsPerformanceSelected(false);
//		performancesAsyncAdapter.updateRowData(start, performances);
//		performancesAsyncAdapter.updateRowCount(length, false);
//		performancesCL.setVisibleRange(start, Constants.visibleRangeLength);
//		performancesCL.redraw();
//	}
//
//	@Override
//	public void refreshPerformances() {
//		setIsPerformanceSelected(false);
//		getUiHandlers().onRangeOrSizeChanged(
//				performancesCL.getVisibleRange().getStart(),
//				performancesCL.getVisibleRange().getLength());
//	}
//
//	public void setIsPerformanceSelected(Boolean selected) {
//		updatePerformance.setVisible(selected);
//		deletePerformance.setVisible(selected);
//		if (!selected) {
//			setDefaultValues();
//		}
//	}
//
//	private void setSelectionModel(CellList<Performance> cl) {
//		final SingleSelectionModel<Performance> selectionModel = new SingleSelectionModel<Performance>();
//
//		selectionModel
//				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
//					@Override
//					public void onSelectionChange(SelectionChangeEvent event) {
//						Performance p = selectionModel.getSelectedObject();
//						setIsPerformanceSelected(null != p);
//						if (null != p) {
//							date.setValue(p.date);
//							show.setValue(p.showName);
//							location.setValue(p.locationName);
//						}
//						getUiHandlers().onPerformanceSelected(p);
//					}
//				});
//
//		cl.setSelectionModel(selectionModel);
//	}
//
//	@UiHandler("createPerformance")
//	void onCreatePerformanceClicked(ClickEvent event) {
//		getUiHandlers().createPerformance(date.getValue(), show.getValue(),
//				location.getValue());
//	}
//
//	@UiHandler("updatePerformance")
//	void onUpdatePerformanceClicked(ClickEvent event) {
//		// TODO how to solve Unchecked cast?
//		final SingleSelectionModel<Performance> selectionModel = (SingleSelectionModel<Performance>) performancesCL
//				.getSelectionModel();
//		getUiHandlers().updatePerformance(
//				selectionModel.getSelectedObject().performanceKey,
//				date.getValue(), show.getValue(), location.getValue());
//	}
//
//	@UiHandler("deletePerformance")
//	void onDeletePerformanceClicked(ClickEvent event) {
//		// TODO how to solve Unchecked cast?
//		final SingleSelectionModel<Performance> selectionModel = (SingleSelectionModel<Performance>) performancesCL
//				.getSelectionModel();
//		getUiHandlers().deletePerformance(
//				selectionModel.getSelectedObject().performanceKey);
//	}
//
//	@Override
//	public void setLocationData(List<Location> locations) {
//		locationSO.clear();
//		for (Location l : locations) {
//			locationSO.add(l.name);
//		}
//	}
//
//	@Override
//	public void setShowData(List<Show> shows) {
//		showSO.clear();
//		for (Show s : shows) {
//			showSO.add(s.name);
//		}
//	}

}
