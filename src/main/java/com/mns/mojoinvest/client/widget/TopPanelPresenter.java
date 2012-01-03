package com.mns.mojoinvest.client.widget;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

public class TopPanelPresenter extends PresenterWidget<TopPanelPresenter.MyView> {

    public interface MyView extends View {
        void highlight(String token);
    }

    private final PlaceManager placeManager;

    @Inject
    public TopPanelPresenter(final EventBus eventBus, final MyView view, final PlaceManager placeManager) {
        super(eventBus, view);
        this.placeManager = placeManager;
    }

    /**
     * {@linkplain TopPanelPresenter.MyView#highlight(String) Highlights} the
     * current place in the {@linkplain TopPanelPresenter.MyView navigation
     * view}.
     *
     * @see com.gwtplatform.mvp.client.PresenterWidget#onReset()
     */
    @Override
    protected void onReset() {
        PlaceRequest request = placeManager.getCurrentPlaceRequest();
        String token = request.getNameToken();
        getView().highlight(token);
    }
}
