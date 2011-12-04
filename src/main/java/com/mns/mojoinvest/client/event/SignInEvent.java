package com.mns.mojoinvest.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class SignInEvent extends GwtEvent<SignInEvent.SignInHandler> {

	public interface HasSignInHandlers extends HasHandlers {
		HandlerRegistration addSignInHandler(SignInHandler handler);
	}

	public interface SignInHandler extends EventHandler {
		void onHasSignIn(SignInEvent event);
	}

	private static Type<SignInHandler> TYPE = new Type<SignInHandler>();

	public static Type<SignInHandler> getType() {
		return TYPE;
	}

	@Override
	public Type<SignInHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SignInHandler handler) {
		handler.onHasSignIn(this);
	}

	public static void fire(HasHandlers source) {
		if (TYPE != null) {
			source.fireEvent(new SignInEvent());
		}
	}

}