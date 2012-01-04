package com.mns.mojoinvest.client.app.component;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class ParamsPresenter extends PresenterWidget<ParamsPresenter.MyView> {

    public interface MyView extends View {
    }

    @Inject
    public ParamsPresenter(final EventBus eventBus, final ParamsPresenter.MyView view) {
        super(eventBus, view);
    }


}
