package com.mns.mojoinvest.client.home;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.mns.mojoinvest.client.MainEntryPoint;
import com.mns.mojoinvest.client.MainPresenter;
import com.mns.mojoinvest.client.NameTokens;

public class HomePresenter extends
        Presenter<HomePresenter.MyView, HomePresenter.MyProxy> implements
        HomeUiHandlers {

    @ProxyStandard
    @NameToken(NameTokens.home)
    public interface MyProxy extends ProxyPlace<HomePresenter> {
    }

    public interface MyView extends View, HasUiHandlers<HomeUiHandlers> {
    }

    @Inject
    public HomePresenter(EventBus eventBus, MyView view, MyProxy proxy) {
        super(eventBus, view, proxy);
        getView().setUiHandlers(this);
    }

    @Override
    protected void onBind() {
        super.onBind();
        MainEntryPoint.logger.info("HomePresenter onBind");
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, MainPresenter.TYPE_RevealPageContent,
                this);
        MainEntryPoint.logger.info("HomePresenter revealInParent");
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        MainEntryPoint.logger.info("HomePresenter onReveal");
    }

    @Override
    protected void onHide() {
        super.onHide();
        MainEntryPoint.logger.info("HomePresenter onHide");
    }
}
