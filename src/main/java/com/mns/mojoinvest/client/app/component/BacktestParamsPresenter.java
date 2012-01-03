package com.mns.mojoinvest.client.app.component;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class BacktestParamsPresenter extends PresenterWidget<BacktestParamsPresenter.MyView> {

    public interface MyView extends View {
    }

    @Inject
    public BacktestParamsPresenter(final EventBus eventBus, final BacktestParamsPresenter.MyView view) {
        super(eventBus, view);
    }
}
