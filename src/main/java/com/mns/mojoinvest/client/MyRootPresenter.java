package com.mns.mojoinvest.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.RootPresenter;

public class MyRootPresenter extends RootPresenter {

    public static final class MyRootView extends RootView {

        @Override
        public void setInSlot(Object slot, Widget widget) {
            RootPanel.get("app").add(widget);
        }
    }

    @Inject
    public MyRootPresenter(EventBus eventBus, MyRootView myRootView) {
        super(eventBus, myRootView);
    }
}