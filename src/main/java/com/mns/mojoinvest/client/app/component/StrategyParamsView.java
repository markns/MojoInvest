package com.mns.mojoinvest.client.app.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.mns.mojoinvest.shared.params.StrategyParams;

public class StrategyParamsView extends Composite
        implements StrategyParamsPresenter.MyView, Editor<StrategyParams> {

    interface StrategyParamsViewUiBinder extends UiBinder<Widget, StrategyParamsView> {
    }

    private static StrategyParamsViewUiBinder uiBinder = GWT.create(StrategyParamsViewUiBinder.class);

    @UiField
    HTMLPanel container;

    public StrategyParamsView() {
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
