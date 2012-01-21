package com.mns.mojoinvest.client.home;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class HomeView extends ViewWithUiHandlers<HomeUiHandlers>
        implements HomePresenter.MyView {

    interface LandingViewUiBinder extends UiBinder<Widget, HomeView> {
    }

    private static LandingViewUiBinder uiBinder = GWT
            .create(LandingViewUiBinder.class);

    public final Widget widget;

    public HomeView() {
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }


}
