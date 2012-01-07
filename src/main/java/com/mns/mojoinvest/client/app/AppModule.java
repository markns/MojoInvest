package com.mns.mojoinvest.client.app;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.mns.mojoinvest.client.app.chart.ChartPresenter;
import com.mns.mojoinvest.client.app.chart.ChartView;
import com.mns.mojoinvest.client.app.params.ParamsPresenter;
import com.mns.mojoinvest.client.app.params.ParamsView;
import com.mns.mojoinvest.client.app.trades.TradesPresenter;
import com.mns.mojoinvest.client.app.trades.TradesView;

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
