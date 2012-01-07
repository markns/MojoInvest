package com.mns.mojoinvest.client.app.params;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.ui.client.ValueBoxEditorDecorator;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.mns.mojoinvest.shared.params.PortfolioParams;

public class PortfolioParamsEditor extends Composite implements Editor<PortfolioParams> {

    interface Binder extends UiBinder<Widget, PortfolioParamsEditor> {
    }

    @UiField
    ValueBoxEditorDecorator<Double> initialInvestment;
    @UiField
    ValueBoxEditorDecorator<Double> transactionCost;

    public PortfolioParamsEditor() {
        initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));
    }
}
