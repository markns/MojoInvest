package com.mns.mojoinvest.client.app.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.mns.mojoinvest.shared.params.BackTestParams;

public class BacktestParamsView extends Composite
        implements BacktestParamsPresenter.MyView, Editor<BackTestParams> {

    interface BacktestParamsViewUiBinder extends UiBinder<Widget, BacktestParamsView> { }

    private static BacktestParamsViewUiBinder uiBinder = GWT.create(BacktestParamsViewUiBinder.class);

    @UiField
    HTMLPanel container;

    public BacktestParamsView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void addToSlot(Object slot, Widget content) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeFromSlot(Object slot, Widget content) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setInSlot(Object slot, Widget content) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
