package com.mns.mojoinvest.client.landing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class LandingView extends ViewWithUiHandlers<LandingUiHandlers>
		implements LandingPresenter.MyView {

	interface LandingViewUiBinder extends UiBinder<Widget, LandingView> {
	}

	private static LandingViewUiBinder uiBinder = GWT
			.create(LandingViewUiBinder.class);

	public final Widget widget;

    public LandingView() {
		widget = uiBinder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}


}
