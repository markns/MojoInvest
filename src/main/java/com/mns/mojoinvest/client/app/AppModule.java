package com.mns.mojoinvest.client.app;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.mns.mojoinvest.client.app.component.*;

public class AppModule extends AbstractPresenterModule {

	@Override
	protected void configure() {

		bindPresenter(AppPresenter.class, AppPresenter.MyView.class,
				AppView.class, AppPresenter.MyProxy.class);

        bindPresenterWidget(BacktestParamsPresenter.class, BacktestParamsPresenter.MyView.class,
                BacktestParamsView.class);
        bindPresenterWidget(StrategyParamsPresenter.class, StrategyParamsPresenter.MyView.class,
                StrategyParamsView.class);
        bindPresenterWidget(ChartPresenter.class, ChartPresenter.MyView.class,
                ChartView.class);
	}
}
