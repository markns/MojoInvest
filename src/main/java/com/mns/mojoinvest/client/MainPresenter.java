package com.mns.mojoinvest.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;

public class MainPresenter extends
        Presenter<MainPresenter.MyView, MainPresenter.MyProxy> implements
        MainUiHandlers {

    @ProxyStandard
    public interface MyProxy extends Proxy<MainPresenter> {
    }

    public interface MyView extends View, HasUiHandlers<MainUiHandlers> {
    }

    /**
     * Use this in leaf presenters, inside their {@link #revealInParent} method.
     */
    @ContentSlot
    public static final Type<RevealContentHandler<?>> TYPE_RevealPageContent = new Type<RevealContentHandler<?>>();

    @Inject
    public MainPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy) {
        super(eventBus, view, proxy);
        getView().setUiHandlers(this);
    }

    @Override
    protected void revealInParent() {
        RevealRootLayoutContentEvent.fire(this, this);
        MainEntryPoint.logger.info("MainPresenter revealInParent");

    }

}
