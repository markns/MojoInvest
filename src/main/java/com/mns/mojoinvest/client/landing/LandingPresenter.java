package com.mns.mojoinvest.client.landing;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.mns.mojoinvest.client.*;
import com.mns.mojoinvest.client.event.UserInfoAvailableEvent;
import com.mns.mojoinvest.shared.dispatch.GetUserAction;
import com.mns.mojoinvest.shared.dispatch.GetUserResult;
import com.mns.mojoinvest.shared.model.UserInfo;

public class LandingPresenter extends
        Presenter<LandingPresenter.MyView, LandingPresenter.MyProxy> implements
        LandingUiHandlers {

    @ProxyStandard
    @NameToken(NameTokens.landing)
    public interface MyProxy extends ProxyPlace<LandingPresenter> {
    }

    public interface MyView extends View, HasUiHandlers<LandingUiHandlers> {
        public void setUserInfo(UserInfo userInfo);

    }

    private final PlaceManager placeManager;
    private final DispatchAsync dispatcher;
    private ClientState clientState;

    @Inject
    public LandingPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                            final PlaceManager placeManager, DispatchAsync dispatcher,
                            final ClientState clientState) {
        super(eventBus, view, proxy);
        getView().setUiHandlers(this);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.clientState = clientState;
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, MainPresenter.TYPE_RevealPageContent,
                this);
        Main.logger.info("Revealing Landing");
    }

    @Override
    protected void onBind() {
        super.onBind();

        // dispatcher.execute(new ReadLocationsAction(
        // "agxjdWx0dXJlc2hvd3NyDgsSB1RoZWF0ZXIY9AIM"),
        // new DispatchCallback<ReadLocationsResult>() {
        // @Override
        // public void onSuccess(ReadLocationsResult result) {
        // Main.logger.info(result.getLocations().toString());
        // }
        // });

        dispatcher.execute(new GetUserAction(Window.Location.getHref()),
                new DispatchCallback<GetUserResult>() {
                    @Override
                    public void onSuccess(GetUserResult result) {
                        if (!result.getErrorText().isEmpty()) {
                            // TODO have a general handler for this
                            Window.alert(result.getErrorText());
                            return;
                        }
                        clientState.userInfo = result.getUserInfo();
//                        clientState.theaters = result.getTheaters();

//                        if (clientState.theaters.size() > 0) {
//                            clientState.currentTheaterKey = clientState.theaters
//                                    .get(0).theaterKey;
                            // TODO set default timezone
                            // http://groups.google.com/group/google-web-toolkit/browse_thread/thread/772e5c9e935a6674/
                            // http://groups.google.com/group/google-web-toolkit/browse_thread/thread/bc293514826ce41b/1fad07b05d4f4df1
                            // http://code.google.com/p/google-web-toolkit/source/browse/trunk/user/test/com/google/gwt/i18n/client/TimeZoneTest.java?r=3655
                            // http://code.google.com/p/google-web-toolkit/issues/detail?id=3489
//                        }
                        // TODO testability broken if relying to global
                        // ClientState
                        onGetUserSuccess();

                    }
                });

        // dispatcher.execute(new
        // GetUserSampleAction(Window.Location.getHref()),
        // new AsyncCallback<GetUserSampleResult>() {
        //
        // @Override
        // public void onFailure(Throwable caught) {
        // Main.logger.severe("GetUserSample call failed "
        // + caught.getMessage());
        // }
        //
        // @Override
        // public void onSuccess(GetUserSampleResult result) {
        // Main.logger.info("GetUserSample result: "
        // + result.getResponse());
        // }
        // });

        // dispatcher.execute(new
        // GetUserSampleAction(Window.Location.getHref()),
        // new DispatchCallback<GetUserSampleResult>() {
        //
        // @Override
        // public void onSuccess(GetUserSampleResult result) {
        // Main.logger.info("GetUserSample result: "
        // + result.getResponse());
        // }
        // });

    }

    public void onGetUserSuccess() {
        Main.logger.info("onGetUserSuccess: User isSignedIn "
                + clientState.userInfo.isSignedIn.toString() + " with email "
                + clientState.userInfo.email + " username "
                + clientState.userInfo.userId);

        UserInfoAvailableEvent.fire(this, clientState);

        if (clientState.userInfo.isSignedIn) {
            PlaceRequest myRequest = new PlaceRequest(NameTokens.app);
            placeManager.revealPlace(myRequest);
        } else {
            getView().setUserInfo(clientState.userInfo);
        }

    }

}
