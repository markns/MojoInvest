package com.mns.mojoinvest.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class DispatchCallback<T> implements AsyncCallback<T> {

	public DispatchCallback() {

	}

	/**
	 * Should be overriden by clients who want to handle error cases themselves.
	 */
	@Override
	public void onFailure(Throwable caught) {
		caught.printStackTrace();
		Window.alert("RPC failed: " + caught.toString());
	}

}
