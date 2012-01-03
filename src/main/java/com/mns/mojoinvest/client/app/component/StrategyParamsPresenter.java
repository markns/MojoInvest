package com.mns.mojoinvest.client.app.component;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class StrategyParamsPresenter extends PresenterWidget<StrategyParamsPresenter.MyView> {

    public interface MyView extends View {
    }

    @Inject
    public StrategyParamsPresenter(final EventBus eventBus, final StrategyParamsPresenter.MyView view) {
        super(eventBus, view);
    }
}
