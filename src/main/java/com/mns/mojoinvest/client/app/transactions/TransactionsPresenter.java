package com.mns.mojoinvest.client.app.transactions;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class TransactionsPresenter extends PresenterWidget<TransactionsPresenter.MyView> {

    public interface MyView extends View { }

    @Inject
    public TransactionsPresenter(final EventBus eventBus, final TransactionsPresenter.MyView view) {
        super(eventBus, view);
    }
}
