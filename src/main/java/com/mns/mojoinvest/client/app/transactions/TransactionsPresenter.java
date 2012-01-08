package com.mns.mojoinvest.client.app.transactions;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.mns.mojoinvest.client.event.RunStrategySuccessEvent;
import com.mns.mojoinvest.shared.dto.TransactionDto;

import java.util.List;

public class TransactionsPresenter extends PresenterWidget<TransactionsPresenter.MyView> {

    public interface MyView extends View {
        void refreshTransactions(List<TransactionDto> transactions);
    }

    @Inject
    public TransactionsPresenter(final EventBus eventBus, final TransactionsPresenter.MyView view) {
        super(eventBus, view);
    }


    @Override
    protected void onBind() {
        super.onBind();
        addRegisteredHandler(RunStrategySuccessEvent.getType(),
                new RunStrategySuccessEvent.RunStrategySuccessHandler() {
                    @Override
                    public void onRunStrategySuccess(RunStrategySuccessEvent event) {
                        getView().refreshTransactions(event.getRunStrategyResult()
                                .getStrategyResult().getTransactions());
                    }
                }
        );
    }
}
