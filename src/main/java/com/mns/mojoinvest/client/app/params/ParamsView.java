package com.mns.mojoinvest.client.app.params;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.mns.mojoinvest.shared.params.Params;

import java.util.List;

public class ParamsView extends ViewWithUiHandlers<ParamsUiHandlers>
        implements ParamsPresenter.MyView, Editor<Params> {

    interface StrategyParamsViewUiBinder extends UiBinder<Widget, ParamsView> {
    }

    private static StrategyParamsViewUiBinder uiBinder = GWT.create(StrategyParamsViewUiBinder.class);

    interface Driver extends SimpleBeanEditorDriver<Params, ParamsView> {
    }

    private static Driver driver = GWT.create(Driver.class);

    @UiField
    PortfolioParamsEditor portfolioParams;
    @UiField
    MomentumStrategyParamsEditor strategyParams;
    @UiField
    FundFilterEditor fundFilter;
    @UiField
    BacktestParamsEditor backtestParams;
    @UiField
    Button runStrategyButton;

    public final Widget widget;

    public ParamsView() {
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setFormationPeriodsAvailable(List<Integer> performanceRangeAcceptable) {
        strategyParams.formationPeriod.setValue(performanceRangeAcceptable.get(0));
        strategyParams.formationPeriod.setAcceptableValues(performanceRangeAcceptable);
    }

    @Override
    public void setProvidersAvailable(List<String> providersAvailable) {
        fundFilter.providers.clear();
        for (String provider : providersAvailable) {
            fundFilter.providers.addItem(provider);
        }
    }

    @Override
    public void setProvidersSelected(List<String> providersSelected) {
        //TODO: Optimise with a different data structure?
        for (int i = 0; i < fundFilter.providers.getItemCount(); i++) {
            if (providersSelected.contains(fundFilter.providers.getItemText(i))) {
                fundFilter.providers.setItemSelected(i, true);
            } else {
                fundFilter.providers.setItemSelected(i, false);
            }
        }
    }

    @Override
    public void setCategoriesAvailable(List<String> categoriesAvailable) {
        fundFilter.categories.clear();
        for (String category : categoriesAvailable) {
            fundFilter.categories.addItem(category);
        }
    }

    @Override
    public void setCategoriesSelected(List<String> categoriesSelected) {
        for (int i = 0; i < fundFilter.categories.getItemCount(); i++) {
            if (categoriesSelected.contains(fundFilter.categories.getItemText(i))) {
                fundFilter.categories.setItemSelected(i, true);
            } else {
                fundFilter.categories.setItemSelected(i, false);
            }
        }
    }

    public void edit(Params params) {
        driver.initialize(this);
        driver.edit(params);
    }

    @UiHandler("runStrategyButton")
    void onSaveButtonClicked(ClickEvent event) {
        if (getUiHandlers() != null) {
            getUiHandlers().runStrategy();
        }
    }

    public Params flush() {
        Params params = driver.flush();

        flushProviders(params);
        flushCategories(params);

        return params;
    }

    private void flushProviders(Params params) {
        params.getFundFilter().getProviders().clear();
        for (int i = 0; i < fundFilter.providers.getItemCount(); i++) {
            if (fundFilter.providers.isItemSelected(i)) {
                params.getFundFilter().getProviders()
                        .add(fundFilter.providers.getItemText(i));
            }
        }
    }

    private void flushCategories(Params params) {
        params.getFundFilter().getCategories().clear();
        for (int i = 0; i < fundFilter.categories.getItemCount(); i++) {
            if (fundFilter.categories.isItemSelected(i)) {
                params.getFundFilter().getCategories()
                        .add(fundFilter.categories.getItemText(i));
            }
        }
    }


}
