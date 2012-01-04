package com.mns.mojoinvest.client.app.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;
import com.gwtplatform.mvp.client.ViewImpl;

public class ParamsView extends ViewImpl
        implements ParamsPresenter.MyView {

    interface StrategyParamsViewUiBinder extends UiBinder<Widget, ParamsView> {
    }

    private static StrategyParamsViewUiBinder uiBinder = GWT.create(StrategyParamsViewUiBinder.class);

    @UiField
    HTMLPanel container;
    @UiField
    Button runStrategyButton;
    @UiField
    DateBox toDate;
    @UiField
    DateBox FromDate;
    @UiField
    TextBox volatilityFilter;
    @UiField
    TextBox portfolioSize;
    @UiField
    TextBox rebalanceFrequency;
    @UiField
    TextBox performanceRange;
    @UiField
    TextBox transactionCost;
    @UiField
    TextBox investmentAmount;

    public final Widget widget;

    public ParamsView() {
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }
}
