package com.mns.mojoinvest.client.dispatch;

import com.google.gwt.http.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.client.actionhandler.AbstractClientActionHandler;
import com.gwtplatform.dispatch.client.actionhandler.ExecuteCommand;
import com.gwtplatform.dispatch.client.actionhandler.UndoCommand;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.Result;

public abstract class AbstractRequestBuilderClientActionHandler<A extends Action<R>, R extends Result>
		extends AbstractClientActionHandler<A, R> {

	protected AbstractRequestBuilderClientActionHandler(
			final Class<A> actionClass) {
		super(actionClass);
	}

	@Override
	public DispatchRequest execute(final A action,
			final AsyncCallback<R> resultCallback,
			final ExecuteCommand<A, R> executeCommand) {

		final RequestBuilder requestBuilder = getRequestBuilder(action);

		requestBuilder.setCallback(new RequestCallback() {

			@Override
			public void onError(final Request request, final Throwable exception) {
				resultCallback.onFailure(exception);
			}

			@Override
			public void onResponseReceived(final Request request,
					final Response response) {
				// TODO handle more errors, such as response.getStatusCode /
				// getStatusText
				resultCallback.onSuccess(extractResult(response));
			}
		});

		try {
			return new DispatchRequestRequestBuilderImpl(requestBuilder.send());
		} catch (final RequestException e) {
			throw new RequestRuntimeException(e);
		}
	}

	protected abstract R extractResult(Response response);

	protected abstract RequestBuilder getRequestBuilder(A action);

	@Override
	public DispatchRequest undo(final A action, final R result,
			final AsyncCallback<Void> callback,
			final UndoCommand<A, R> undoCommand) {
		throw new UnsupportedOperationException();
	}
}
