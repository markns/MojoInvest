package com.mns.alphaposition.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.Title;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.mns.alphaposition.client.NameTokens;
import com.mns.alphaposition.client.view.HomeUiHandlers;


public class HomePresenter extends Presenter<HomePresenter.MyView, HomePresenter.MyProxy>
        implements HomeUiHandlers {
    /**
     * {@link HomePresenter}'s proxy.
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.homePage)
    @Title("Home")
    public interface MyProxy extends ProxyPlace<HomePresenter> {
    }

    /**
     * {@link HomePresenter}'s view.
     */
    public interface MyView extends View, HasUiHandlers<HomeUiHandlers> {
    }

    private PlaceManager placeManager;

    @Inject
    public HomePresenter(final EventBus eventBus, final MyView view,
                         final MyProxy proxy, final PlaceManager placeManager) {
        super(eventBus, view, proxy);
        this.placeManager = placeManager;
        view.setUiHandlers(this);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, BreadcrumbsPresenter.TYPE_SetMainContent,
                this);
    }

    @Override
    public void revealAllProductsList() {
        placeManager.revealRelativePlace(new PlaceRequest(NameTokens.productList).with(
                ProductListPresenter.TOKEN_TYPE, ProductListPresenter.TYPE_ALL_PRODUCTS));
    }

//  @Override
//  public void revealFavoriteProductsList() {
//    placeManager.revealRelativePlace(new PlaceRequest(NameTokens.productList).with(
//        ProductListPresenter.TOKEN_TYPE,
//        ProductListPresenter.TYPE_FAVORITE_PRODUCTS));
//  }
//
//  @Override
//  public void revealSpecialsList() {
//    placeManager.revealRelativePlace(new PlaceRequest(NameTokens.productList).with(
//        ProductListPresenter.TOKEN_TYPE, ProductListPresenter.TYPE_SPECIALS));
//  }

}