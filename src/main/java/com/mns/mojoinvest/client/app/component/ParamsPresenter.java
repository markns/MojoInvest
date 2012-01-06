package com.mns.mojoinvest.client.app.component;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

import java.util.List;

public class ParamsPresenter extends PresenterWidget<ParamsPresenter.MyView>
        implements ParamsUiHandlers {

    public interface MyView extends View, HasUiHandlers<ParamsUiHandlers> {
        void setPerformanceRangesAvailable(List<Integer> performanceRangeAcceptable);
        void setProvidersAvailable(List<String> providersAvailable);
        void setCategoriesAvailable(List<String> categoriesAvailable);
        void edit(Person person);
        Person flush();
    }

    @Inject
    public ParamsPresenter(final EventBus eventBus, final ParamsPresenter.MyView view) {
        super(eventBus, view);
        getView().setUiHandlers(this);
    }

    @Override
    public void run() {
        Person person = getView().flush();
        Window.alert(person.toString());
    }
}
