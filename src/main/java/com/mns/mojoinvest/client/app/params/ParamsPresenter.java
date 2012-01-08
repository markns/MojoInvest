package com.mns.mojoinvest.client.app.params;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.mns.mojoinvest.client.event.RunStrategySuccessEvent;
import com.mns.mojoinvest.shared.dispatch.RunStrategyAction;
import com.mns.mojoinvest.shared.dispatch.RunStrategyResult;
import com.mns.mojoinvest.shared.params.Params;

import java.util.List;

public class ParamsPresenter extends PresenterWidget<ParamsPresenter.MyView>
        implements ParamsUiHandlers {

    public interface MyView extends View, HasUiHandlers<ParamsUiHandlers> {
        void setFormationPeriodsAvailable(List<Integer> performanceRangeAcceptable);
        void setProvidersAvailable(List<String> providersAvailable);
        void setProvidersSelected(List<String> providers);
        void setCategoriesAvailable(List<String> categoriesAvailable);
        void setCategoriesSelected(List<String> categories);
        void edit(Params params);
        Params flush();
    }

    private final DispatchAsync dispatcher;

    @Inject
    public ParamsPresenter(final EventBus eventBus, final ParamsPresenter.MyView view,
                           DispatchAsync dispatcher) {
        super(eventBus, view);
        this.dispatcher = dispatcher;
        getView().setUiHandlers(this);
    }

    @Override
    public void runStrategy() {

        Params params = getView().flush();

        Window.alert(params.toString());

        dispatcher.execute(new RunStrategyAction(params), new AsyncCallback<RunStrategyResult>() {
            @Override
            public void onSuccess(RunStrategyResult result) {
                //TODO: Not sure about the ParamsPresenter.this reference
                RunStrategySuccessEvent.fire(ParamsPresenter.this, result, true);
            }
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Strategy failed to execute successfully: " + caught);
                //TODO: Broadcast run strategy failure event
            }
        });
    }
}
