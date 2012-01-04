package com.mns.mojoinvest.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.mns.mojoinvest.client.resources.Resources;
import com.mns.mojoinvest.client.util.UiUtils;

public class MainView extends ViewWithUiHandlers<MainUiHandlers> implements
        MainPresenter.MyView {

    interface LandingViewUiBinder extends UiBinder<Widget, MainView> {
    }

    private static LandingViewUiBinder uiBinder = GWT.create(LandingViewUiBinder.class);

    private final Resources resources;

    @UiField
    LayoutPanel topPanel;

    @UiField
    LayoutPanel pageContent;

    public final Widget widget;

    @Inject
    public MainView(Resources resources) {
        // Inject the global CSS resources
        this.resources = resources;
        this.resources.mojo().ensureInjected();
        this.resources.widgets().ensureInjected();
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {
        if (slot == MainPresenter.SLOT_Navigation) {
            UiUtils.setContent(topPanel, content);
        } else if (slot == MainPresenter.TYPE_RevealPageContent) {
            UiUtils.setContent(pageContent, content);
        } else {
            super.setInSlot(slot, content);
        }
    }

}
