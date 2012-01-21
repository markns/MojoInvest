package com.mns.mojoinvest.client.app;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.dispatch.shared.Result;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ManualRevealCallback;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.mns.mojoinvest.client.MainEntryPoint;
import com.mns.mojoinvest.client.MainPresenter;
import com.mns.mojoinvest.client.NameTokens;
import com.mns.mojoinvest.client.app.chart.ChartPresenter;
import com.mns.mojoinvest.client.app.params.ParamsPresenter;
import com.mns.mojoinvest.client.app.transactions.TransactionsPresenter;
import com.mns.mojoinvest.shared.action.BatchAction;
import com.mns.mojoinvest.shared.action.BatchResult;
import com.mns.mojoinvest.shared.dispatch.*;
import com.mns.mojoinvest.shared.params.Params;

import java.util.ArrayList;
import java.util.List;

public class AppPresenter extends Presenter<AppPresenter.MyView, AppPresenter.MyProxy>
        implements AppUiHandlers {

    @ProxyCodeSplit
    @NameToken(NameTokens.app)
    public interface MyProxy extends ProxyPlace<AppPresenter> {
    }

    public interface MyView extends View, HasUiHandlers<AppUiHandlers> {
    }

    public static final Object SLOT_transactions = new Object();
    public static final Object SLOT_params = new Object();
    public static final Object SLOT_chart = new Object();

    private final DispatchAsync dispatcher;

    private final TransactionsPresenter transactionsPresenter;
    private final ParamsPresenter paramsPresenter;
    private final ChartPresenter chartPresenter;

    @Inject
    public AppPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                        DispatchAsync dispatcher,
                        TransactionsPresenter transactionsPresenter,
                        ParamsPresenter paramsPresenter,
                        ChartPresenter chartPresenter) {
        super(eventBus, view, proxy);
        this.dispatcher = dispatcher;
        this.transactionsPresenter = transactionsPresenter;
        this.paramsPresenter = paramsPresenter;
        this.chartPresenter = chartPresenter;
        getView().setUiHandlers(this);

    }

    @Override
    public boolean useManualReveal() {
        return true;
    }

    private interface OnSuccessCallback<T extends Result> {
        void onSuccess(T result);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);

        List<Action> actions = new ArrayList<Action>();
        final List<OnSuccessCallback> successCallbacks = new ArrayList<OnSuccessCallback>();

        //TODO: Move the collection of the actions to the presenter widgets
        //paramsPresenter.getInitActions();
        //TODO: add the child actions and onSuccessHandler directly to the BatchAction

        actions.add(new GetPerformanceRangesAvailableAction());
        successCallbacks.add(new OnSuccessCallback<GetPerformanceRangesAvailableResult>() {
            @Override
            public void onSuccess(GetPerformanceRangesAvailableResult result) {
                MainEntryPoint.logger.info(result.toString());
                paramsPresenter.getView()
                        .setFormationPeriodsAvailable(result.getPerformanceRangesAvailable());
            }
        });

        actions.add(new GetProvidersAvailableAction());
        successCallbacks.add(new OnSuccessCallback<GetProvidersAvailableResult>() {
            @Override
            public void onSuccess(GetProvidersAvailableResult result) {
                paramsPresenter.getView()
                        .setProvidersAvailable(result.getProvidersAvailable());
            }
        });

        actions.add(new GetCategoriesAvailableAction());
        successCallbacks.add(new OnSuccessCallback<GetCategoriesAvailableResult>() {
            @Override
            public void onSuccess(GetCategoriesAvailableResult result) {
                paramsPresenter.getView()
                        .setCategoriesAvailable(result.getCategoriesAvailable());
            }
        });


        actions.add(new GetParamDefaultsAction());
        successCallbacks.add(new OnSuccessCallback<GetParamDefaultsResult>() {
            @Override
            public void onSuccess(GetParamDefaultsResult result) {
                MainEntryPoint.logger.info(result.toString());
                Params params = result.getParams();
                paramsPresenter.getView().edit(params);
                paramsPresenter.getView()
                        .setCategoriesSelected(params.getFundFilter().getCategories());
                paramsPresenter.getView()
                        .setProvidersSelected(params.getFundFilter().getProviders());
            }
        });


        BatchAction batch = new BatchAction(BatchAction.OnException.ROLLBACK,
                actions.toArray(new Action[actions.size()]));

        dispatcher.execute(batch, ManualRevealCallback.create(this,
                new AsyncCallback<BatchResult>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public void onSuccess(BatchResult batchResult) {
                        for (int i = 0; i < batchResult.getResults().size(); i++) {
                            successCallbacks.get(i).onSuccess(batchResult.getResults().get(i));
                        }
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.toString());
                        MainEntryPoint.logger.severe(caught.toString());
                        caught.printStackTrace();
                    }
                }
        ));
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, MainPresenter.TYPE_RevealPageContent, this);
        MainEntryPoint.logger.info("AppPresenter revealInParent");
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        MainEntryPoint.logger.info("AppPresenter onReveal");
        setInSlot(SLOT_transactions, transactionsPresenter);
        setInSlot(SLOT_params, paramsPresenter);
        setInSlot(SLOT_chart, chartPresenter);
    }

    @Override
    public void getPerformance(String symbol) {
        MainEntryPoint.logger.info("Requested performance for " + symbol);
    }
}
