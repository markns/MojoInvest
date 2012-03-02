package com.mns.mojoinvest.client.app.params;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.text.client.IntegerRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import com.mns.mojoinvest.shared.params.MomentumStrategyParams;

public class MomentumStrategyParamsEditor extends Composite
        implements Editor<MomentumStrategyParams> {

    interface Binder extends UiBinder<Widget, MomentumStrategyParamsEditor> {
    }

    //TODO: Slider article: http://www.zackgrossbart.com/hackito/gwt-slider/

    @UiField(provided = true)
    ValueListBox<Integer> formationPeriod = new ValueListBox<Integer>(IntegerRenderer.instance());
    @UiField(provided = true)
    ValueListBox<Integer> holdingPeriod = new ValueListBox<Integer>(IntegerRenderer.instance());
    @UiField(provided = true)
    ValueListBox<Integer> portfolioSize = new ValueListBox<Integer>(IntegerRenderer.instance());

    public MomentumStrategyParamsEditor() {
        initWidget(GWT.<Binder>create(Binder.class).createAndBindUi(this));
    }
}
