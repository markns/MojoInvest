package com.mns.mojoinvest.client.navigation;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class NavigationModule extends AbstractPresenterModule {

	@Override
	protected void configure() {

        bindPresenterWidget(NavigationPresenter.class,
                NavigationPresenter.MyView.class, NavigationView.class);
	}
}
