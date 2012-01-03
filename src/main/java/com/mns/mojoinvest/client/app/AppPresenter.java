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
import com.mns.mojoinvest.client.app.component.BacktestParamsPresenter;
import com.mns.mojoinvest.client.app.component.ChartPresenter;
import com.mns.mojoinvest.client.app.component.StrategyParamsPresenter;

public class AppPresenter extends Presenter<AppPresenter.MyView, AppPresenter.MyProxy>
        implements AppUiHandlers {

    @ProxyCodeSplit
    @NameToken(NameTokens.app)
    public interface MyProxy extends ProxyPlace<AppPresenter> {
    }

    public interface MyView extends View, HasUiHandlers<AppUiHandlers> {
    }

    public static final Object SLOT_backtest = new Object();
    public static final Object SLOT_strategy = new Object();
    public static final Object SLOT_chart = new Object();

    private final BacktestParamsPresenter backtestParamsPresenter;
    private final StrategyParamsPresenter strategyParamsPresenter;
    private final ChartPresenter chartPresenter;

    @Inject
    public AppPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                        BacktestParamsPresenter backtestParamsPresenter,
                        StrategyParamsPresenter strategyParamsPresenter,
                        ChartPresenter chartPresenter) {
        super(eventBus, view, proxy);
        this.backtestParamsPresenter = backtestParamsPresenter;
        this.strategyParamsPresenter = strategyParamsPresenter;
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
        setInSlot(SLOT_backtest, backtestParamsPresenter);
        setInSlot(SLOT_strategy, strategyParamsPresenter);
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
