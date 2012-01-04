package com.mns.mojoinvest.client.app.component;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class TradesPresenter extends PresenterWidget<TradesPresenter.MyView> {

    public interface MyView extends View { }

    @Inject
    public TradesPresenter(final EventBus eventBus, final TradesPresenter.MyView view) {
        super(eventBus, view);
    }
}
