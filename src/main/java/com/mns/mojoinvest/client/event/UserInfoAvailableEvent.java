package com.mns.mojoinvest.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.mns.mojoinvest.client.ClientState;

public class UserInfoAvailableEvent extends
        GwtEvent<UserInfoAvailableEvent.UserInfoAvailableHandler> {

	public interface HasUserInfoAvailableHandlers extends HasHandlers {
		HandlerRegistration addUserInfoAvailableHandler(
                UserInfoAvailableHandler handler);
	}

	public interface UserInfoAvailableHandler extends EventHandler {
		void onHasUserInfoAvailable(UserInfoAvailableEvent event);
	}

	private static Type<UserInfoAvailableHandler> TYPE = new Type<UserInfoAvailableHandler>();

	public static Type<UserInfoAvailableHandler> getType() {
		return TYPE;
	}

	@Override
	public Type<UserInfoAvailableHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UserInfoAvailableHandler handler) {
		handler.onHasUserInfoAvailable(this);
	}

	public static void fire(HasHandlers source, ClientState clientState) {
		if (TYPE != null) {
			source.fireEvent(new UserInfoAvailableEvent(clientState));
		}
	}

	private ClientState clientState;

	public UserInfoAvailableEvent(ClientState clientState) {
		this.clientState = clientState;
	}

	public ClientState getClientState() {
		return this.clientState;
	}

}