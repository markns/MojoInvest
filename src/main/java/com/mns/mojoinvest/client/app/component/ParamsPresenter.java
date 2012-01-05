package com.mns.mojoinvest.client.app.component;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

import java.util.List;

public class ParamsPresenter extends PresenterWidget<ParamsPresenter.MyView> {

    public interface MyView extends View {
        void setPerformanceRangeAcceptable(List<Integer> performanceRangeAcceptable);
    }

    @Inject
    public ParamsPresenter(final EventBus eventBus, final ParamsPresenter.MyView view) {
        super(eventBus, view);
    }

    @Override
    protected void onBind() {
        super.onBind();
    }

}
