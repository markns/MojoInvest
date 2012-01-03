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
import com.mns.mojoinvest.client.navigation.NavigationPresenter;

public class MainPresenter extends
        Presenter<MainPresenter.MyView, MainPresenter.MyProxy> implements
        MainUiHandlers {

    @ProxyStandard
    public interface MyProxy extends Proxy<MainPresenter> {
    }

    public interface MyView extends View, HasUiHandlers<MainUiHandlers> {
    }

    /**
     * Constant for the static navigation slot.
     */
    public static final Object SLOT_Navigation = new Object();

    /**
     * Use this in leaf presenters, inside their {@link #revealInParent} method.
     */
    @ContentSlot
    public static final Type<RevealContentHandler<?>> TYPE_RevealPageContent = new Type<RevealContentHandler<?>>();

    private final NavigationPresenter navigationPresenter;

    @Inject
    public MainPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy,
                         final NavigationPresenter navigationPresenter) {
        super(eventBus, view, proxy);
        this.navigationPresenter = navigationPresenter;
        getView().setUiHandlers(this);
    }

    @Override
    protected void onBind() {
        super.onBind();
        Main.logger.info("MainPresenter onBind");
    }

    @Override
    protected void revealInParent() {
        RevealRootLayoutContentEvent.fire(this, this);
        Main.logger.info("MainPresenter revealInParent");

    }

    /**
     * Sets {@link com.mns.mojoinvest.client.navigation.NavigationPresenter} in {@link #SLOT_Navigation}
     *
     * @see com.gwtplatform.mvp.client.PresenterWidget#onReveal()
     */
    @Override
    public void onReveal() {
        super.onReveal();
        Main.logger.info("MainPresenter onReveal");

        setInSlot(SLOT_Navigation, navigationPresenter);
    }

    @Override
    protected void onReset() {
        super.onReset();
        Main.logger.info("MainPresenter onReset");
    }

    @Override
    protected void onHide() {
        super.onHide();
        Main.logger.info("MainPresenter onHide");
    }


}
