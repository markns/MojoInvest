package com.mns.mojoinvest.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.mns.mojoinvest.client.util.UiUtils;

public class MainView extends ViewWithUiHandlers<MainUiHandlers> implements
        MainPresenter.MyView {

    interface LandingViewUiBinder extends UiBinder<Widget, MainView> {
    }

    private static LandingViewUiBinder uiBinder = GWT.create(LandingViewUiBinder.class);

    @UiField
    FlowPanel pageContent;

    public final Widget widget;

    @Inject
    public MainView() {
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {
        if (slot == MainPresenter.TYPE_RevealPageContent) {
            UiUtils.setContent(pageContent, content);
        } else {
            super.setInSlot(slot, content);
        }
    }

}
