package com.mns.mojoinvest.client.landing;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.mns.mojoinvest.client.Main;
import com.mns.mojoinvest.client.MainPresenter;
import com.mns.mojoinvest.client.NameTokens;

public class LandingPresenter extends
        Presenter<LandingPresenter.MyView, LandingPresenter.MyProxy> implements
        LandingUiHandlers {

    @ProxyStandard
    @NameToken(NameTokens.landing)
    public interface MyProxy extends ProxyPlace<LandingPresenter> {
    }

    public interface MyView extends View, HasUiHandlers<LandingUiHandlers> {
    }

    @Inject
    public LandingPresenter(EventBus eventBus, MyView view, MyProxy proxy) {
        super(eventBus, view, proxy);
        getView().setUiHandlers(this);
    }

    @Override
    protected void onBind() {
        super.onBind();
        Main.logger.info("LandingPresenter onBind");
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, MainPresenter.TYPE_RevealPageContent,
                this);
        Main.logger.info("LandingPresenter revealInParent");
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        Main.logger.info("LandingPresenter onReveal");
    }

    @Override
    protected void onHide() {
        super.onHide();
        Main.logger.info("LandingPresenter onHide");
    }
}
