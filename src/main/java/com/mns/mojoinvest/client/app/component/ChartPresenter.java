package com.mns.mojoinvest.client.app.component;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class ChartPresenter extends PresenterWidget<ChartPresenter.MyView> {

    public interface MyView extends View {
    }

    @Inject
    public ChartPresenter(final EventBus eventBus, final ChartPresenter.MyView view) {
        super(eventBus, view);
    }
}
