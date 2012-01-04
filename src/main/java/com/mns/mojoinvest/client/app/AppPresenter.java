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
import com.mns.mojoinvest.client.app.component.TradesPresenter;
import com.mns.mojoinvest.client.app.component.ChartPresenter;
import com.mns.mojoinvest.client.app.component.ParamsPresenter;

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

    private final TradesPresenter tradesPresenter;
    private final ParamsPresenter paramsPresenter;
    private final ChartPresenter chartPresenter;

    @Inject
    public AppPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                        TradesPresenter tradesPresenter,
                        ParamsPresenter paramsPresenter,
                        ChartPresenter chartPresenter) {
        super(eventBus, view, proxy);
        this.tradesPresenter = tradesPresenter;
        this.paramsPresenter = paramsPresenter;
        this.chartPresenter = chartPresenter;
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
