package com.mns.mojoinvest.client.landing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.mns.mojoinvest.client.Main;
import com.mns.mojoinvest.shared.model.UserInfo;

public class LandingView extends ViewWithUiHandlers<LandingUiHandlers>
		implements LandingPresenter.MyView {

	interface LandingViewUiBinder extends UiBinder<Widget, LandingView> {
	}

	private static LandingViewUiBinder uiBinder = GWT
			.create(LandingViewUiBinder.class);

	public final Widget widget;

	private UserInfo userInfo;

	@UiField
	HTMLPanel signInContainer;

	public LandingView() {
		widget = uiBinder.createAndBindUi(this);

	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
		if (!userInfo.isSignedIn) {
			signInContainer.setVisible(true);
		}
	}

	@UiHandler("googleSignIn")
	void onGoogleSignInClicked(ClickEvent event) {
		Main.logger.info("redirecting to " + userInfo.signInURLs.get("Google"));
		Window.Location.replace(userInfo.signInURLs.get("Google"));
	}

//	@UiHandler("yahooSignIn")
//	void onYahooSignInClicked(ClickEvent event) {
//		Window.Location.replace(userInfo.signInURLs.get("Yahoo"));
//	}

}
