package com.mns.mojoinvest.client.app;

import com.google.gwt.event.shared.EventBus;
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
import com.mns.mojoinvest.client.Main;
import com.mns.mojoinvest.client.MainPresenter;
import com.mns.mojoinvest.client.NameTokens;
import com.mns.mojoinvest.client.app.component.ChartPresenter;
import com.mns.mojoinvest.client.app.component.ParamsPresenter;
import com.mns.mojoinvest.client.app.component.TradesPresenter;
import com.mns.mojoinvest.shared.action.BatchAction;
import com.mns.mojoinvest.shared.action.BatchResult;
import com.mns.mojoinvest.shared.dispatch.GetParamDefaultsAction;
import com.mns.mojoinvest.shared.dispatch.GetParamDefaultsResult;
import com.mns.mojoinvest.shared.dispatch.GetPerformanceRangesAvailableAction;
import com.mns.mojoinvest.shared.dispatch.GetPerformanceRangesAvailableResult;

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

    public static final Object SLOT_trades = new Object();
    public static final Object SLOT_params = new Object();
    public static final Object SLOT_chart = new Object();

    private final DispatchAsync dispatcher;

    private final TradesPresenter tradesPresenter;
    private final ParamsPresenter paramsPresenter;
    private final ChartPresenter chartPresenter;

    @Inject
    public AppPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                        DispatchAsync dispatcher,
                        TradesPresenter tradesPresenter,
                        ParamsPresenter paramsPresenter,
                        ChartPresenter chartPresenter) {
        super(eventBus, view, proxy);
        this.dispatcher = dispatcher;
        this.tradesPresenter = tradesPresenter;
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

        actions.add(new GetParamDefaultsAction());
        successCallbacks.add(new OnSuccessCallback<GetParamDefaultsResult>() {
            @Override
            public void onSuccess(GetParamDefaultsResult result) {
                Main.logger.info(result.toString());
            }
        });

        actions.add(new GetPerformanceRangesAvailableAction());
        successCallbacks.add(new OnSuccessCallback<GetPerformanceRangesAvailableResult>() {
            @Override
            public void onSuccess(GetPerformanceRangesAvailableResult result) {
                Main.logger.info(result.toString());
                paramsPresenter.getView()
                        .setPerformanceRangeAcceptable(result.getPerformanceRangesAvailable());
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
                        Main.logger.severe(caught + " " + caught.getMessage());
                        caught.printStackTrace();
                    }
                }
        ));
    }


    @Override
    protected void onBind() {
        super.onBind();
        Main.logger.info("AppPresenter onBind");
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, MainPresenter.TYPE_RevealPageContent, this);
        Main.logger.info("AppPresenter revealInParent");
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        Main.logger.info("AppPresenter onReveal");
        setInSlot(SLOT_trades, tradesPresenter);
        setInSlot(SLOT_params, paramsPresenter);
        setInSlot(SLOT_chart, chartPresenter);
    }

    @Override
    protected void onReset() {
        super.onReset();
        Main.logger.info("AppPresenter onReset");
    }

    @Override
    protected void onHide() {
        super.onHide();
        Main.logger.info("AppPresenter onHide");
    }

}
