package com.mns.mojoinvest.client.app;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class AppModule extends AbstractPresenterModule {

	@Override
	protected void configure() {

		bindPresenter(AppPresenter.class, AppPresenter.MyView.class,
				AppView.class, AppPresenter.MyProxy.class);

	}
}
