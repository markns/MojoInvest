package com.mns.mojoinvest.client;

import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;

public class SignedInGatekeeper implements Gatekeeper {

	@Inject
	private ClientState clientState;

	@Override
	public boolean canReveal() {
		if (null != clientState.userInfo) {
			if (clientState.userInfo.isSignedIn) {
				return true;
			}
		}

		return false;
	}
}
