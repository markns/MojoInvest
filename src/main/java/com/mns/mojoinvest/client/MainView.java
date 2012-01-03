package com.mns.mojoinvest.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.mns.mojoinvest.client.widget.TopPanel;

public class MainView extends ViewWithUiHandlers<MainUiHandlers> implements
		MainPresenter.MyView {

	interface LandingViewUiBinder extends UiBinder<Widget, MainView> {
	}

	private static LandingViewUiBinder uiBinder = GWT
			.create(LandingViewUiBinder.class);

	public final Widget widget;

    @UiField
    TopPanel topPanel;

//	@UiField
//    SimplePanel headerNav;
    @UiField
    SimplePanel pageContent;

    public MainView() {
		widget = uiBinder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(Object slot, Widget content) {
		if (slot == MainPresenter.TYPE_RevealHeaderContent) {

//			topPanel.clear();
//			topPanel.add(content);
		} else if (slot == MainPresenter.TYPE_RevealPageContent) {
			pageContent.clear();
			pageContent.add(content);
		} else {
			super.setInSlot(slot, content);
		}
	}

}
