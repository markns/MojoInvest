package com.mns.mojoinvest.client.app;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.mns.mojoinvest.client.app.component.*;

public class AppModule extends AbstractPresenterModule {

	@Override
	protected void configure() {

		bindPresenter(AppPresenter.class, AppPresenter.MyView.class,
				AppView.class, AppPresenter.MyProxy.class);

        bindPresenterWidget(ParamsPresenter.class, ParamsPresenter.MyView.class,
                ParamsView.class);
        bindPresenterWidget(ChartPresenter.class, ChartPresenter.MyView.class,
                ChartView.class);
        bindPresenterWidget(TradesPresenter.class, TradesPresenter.MyView.class,
                TradesView.class);
    }
}
