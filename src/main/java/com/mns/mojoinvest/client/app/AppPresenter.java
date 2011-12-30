package com.mns.mojoinvest.client.app;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.visualization.client.DataTable;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.mns.mojoinvest.client.*;
import com.mns.mojoinvest.shared.dispatch.GetFundPerformanceAction;
import com.mns.mojoinvest.shared.dispatch.GetFundPerformanceResult;
import com.mns.mojoinvest.shared.dto.OptionsDto;

public class AppPresenter extends
        Presenter<AppPresenter.MyView, AppPresenter.MyProxy>
        implements AppUiHandlers {

    @ProxyStandard
    @NameToken(NameTokens.app)
//    @UseGatekeeper(SignedInGatekeeper.class)
    public interface MyProxy extends ProxyPlace<AppPresenter> {

    }

    public interface MyView extends View, HasUiHandlers<AppUiHandlers> {

        public void resetAndFocus();

        public void setDefaultValues();

        public void setChartData(DataTable dataTable, OptionsDto optionsDto);

    }

    private final PlaceManager placeManager;

    private final DispatchAsync dispatcher;
    private ClientState clientState;

    @Inject
    public AppPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                        PlaceManager placeManager, DispatchAsync dispatcher,
                        final ClientState clientState) {
        super(eventBus, view, proxy);
        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.clientState = clientState;

        getView().setUiHandlers(this);

    }

    @Override
    protected void onReset() {
        super.onReset();
        getView().resetAndFocus();

    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, MainPresenter.TYPE_RevealPageContent, this);
//		getPerformance("ALD");
    }

    @Override
    public void getPerformance(String symbol) {
        Main.logger.info("Requested performance for " + symbol);

        dispatcher.execute(new GetFundPerformanceAction(symbol),
                new DispatchCallback<GetFundPerformanceResult>() {
                    @Override
                    public void onSuccess(GetFundPerformanceResult result) {
						if (!result.getErrorText().isEmpty()) {
							Window.alert(result.getErrorText());
							return;
						}
                        getView().setDefaultValues();
                        getView().setChartData(result.getDataTableDto().getDataTable(),
                                result.getOptionsDto());
                    }
                });

    }

}
