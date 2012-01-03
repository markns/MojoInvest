package com.mns.mojoinvest.client.app.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

public class StrategyParamsView extends ViewImpl
        implements StrategyParamsPresenter.MyView {

    interface StrategyParamsViewUiBinder extends UiBinder<Widget, StrategyParamsView> {
    }

    private static StrategyParamsViewUiBinder uiBinder = GWT.create(StrategyParamsViewUiBinder.class);

    @UiField
    HTMLPanel container;

    public final Widget widget;

    public StrategyParamsView() {
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }
}
