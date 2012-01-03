package com.mns.mojoinvest.client.widget;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class WidgetModule extends AbstractPresenterModule {

	@Override
	protected void configure() {

        bindPresenterWidget(TopPanelPresenter.class,
                TopPanelPresenter.MyView.class, TopPanelView.class);
	}
}
