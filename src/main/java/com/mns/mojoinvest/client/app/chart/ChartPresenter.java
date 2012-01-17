package com.mns.mojoinvest.client.app.chart;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.mns.mojoinvest.client.event.RunStrategySuccessEvent;

public class ChartPresenter extends PresenterWidget<ChartPresenter.MyView> {

    public interface MyView extends View {
        void createChart(DataTable dataTable, Options options);
    }

    @Inject
    public ChartPresenter(final EventBus eventBus, final ChartPresenter.MyView view) {
        super(eventBus, view);
    }

    @Override
    protected void onBind() {
        super.onBind();
        addRegisteredHandler(RunStrategySuccessEvent.getType(),
                new RunStrategySuccessEvent.RunStrategySuccessHandler() {
                    @Override
                    public void onRunStrategySuccess(RunStrategySuccessEvent event) {
                        getView().createChart(event.getRunStrategyResult()
                                .getStrategyResult().getDataTableDto().getDataTable(), createOptions());
                    }
                }
        );
    }

    private Options createOptions() {
        Options options = Options.create();
//        options.setLegend(LegendPosition.NONE);
        return options;
    }
}
