package com.mns.mojoinvest.client.app;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.mns.mojoinvest.client.Main;
import com.mns.mojoinvest.client.MainPresenter;
import com.mns.mojoinvest.client.NameTokens;

public class AppPresenter extends Presenter<AppPresenter.MyView, AppPresenter.MyProxy>
        implements AppUiHandlers {

    public interface MyView extends View, HasUiHandlers<AppUiHandlers> {
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.app)
    public interface MyProxy extends ProxyPlace<AppPresenter> { }

    @Inject
    public AppPresenter(EventBus eventBus, MyView view, MyProxy proxy) {
        super(eventBus, view, proxy);
        getView().setUiHandlers(this);
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

//    @Override
//    public void getPerformance(String symbol) {
//        Main.logger.info("Requested performance for " + symbol);
//
//        dispatcher.execute(new GetFundPerformanceAction(symbol),
//                new DispatchCallback<GetFundPerformanceResult>() {
//                    @Override
//                    public void onSuccess(GetFundPerformanceResult result) {
//						if (!result.getErrorText().isEmpty()) {
//							Window.alert(result.getErrorText());
//							return;
//						}
//                        getView().setDefaultValues();
//                        getView().setChartData(result.getDataTableDto().getDataTable(),
//                                result.getOptionsDto());
//                    }
//                });
//
//    }

}
