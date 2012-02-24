package com.mns.mojoinvest.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl;
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import com.mns.mojoinvest.client.resources.Resources;
import com.mns.mojoinvest.client.resources.Translations;

public class MainModule extends AbstractPresenterModule {

    @Override
    protected void configure() {

        // Default implementation of standard resources
//        install(new DefaultModule(MainPlaceManager.class));
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(Singleton.class);
        bind(RootPresenter.class).to(MyRootPresenter.class).asEagerSingleton();
        bind(GoogleAnalytics.class).to(GoogleAnalyticsImpl.class).in(Singleton.class);
        bind(PlaceManager.class).to(MainPlaceManager.class).in(Singleton.class);

        bind(Resources.class).in(Singleton.class);
        bind(Translations.class).in(Singleton.class);
        bind(ClientState.class).in(Singleton.class);
        // bind(SignedInGatekeeper.class).in(Singleton.class);

        // Constants
        bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.app);

        bindPresenter(MainPresenter.class, MainPresenter.MyView.class,
                MainView.class, MainPresenter.MyProxy.class);
    }
}