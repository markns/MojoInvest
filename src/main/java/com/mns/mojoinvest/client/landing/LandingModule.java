package com.mns.mojoinvest.client.landing;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class LandingModule extends AbstractPresenterModule {

	@Override
	protected void configure() {

		bindPresenter(LandingPresenter.class, LandingPresenter.MyView.class,
				LandingView.class, LandingPresenter.MyProxy.class);

	}
}
