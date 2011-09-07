package com.mns.alphaposition.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.*;

/**
 * This is the top-level presenter of the hierarchy. Other presenters reveal
 * themselves within this presenter. This presenter display a breadcrumbs with
 * the titles of the previously visited pages.
 */
public class BreadcrumbsPresenter extends
        Presenter<BreadcrumbsPresenter.MyView, BreadcrumbsPresenter.MyProxy> {
    /**
     * {@link BreadcrumbsPresenter}'s proxy.
     */
    @ProxyStandard
    public interface MyProxy extends Proxy<BreadcrumbsPresenter> {
    }

    /**
     * {@link BreadcrumbsPresenter}'s view.
     */
    public interface MyView extends View {
        void clearBreadcrumbs(int breadcrumbSize);

        void setBreadcrumbs(int index, String title);
    }

    /**
     * Use this in leaf presenters, inside their {@link #revealInParent} method.
     */
    @ContentSlot
    public static final Type<RevealContentHandler<?>> TYPE_SetMainContent = new Type<RevealContentHandler<?>>();

    private final PlaceManager placeManager;

    @Inject
    public BreadcrumbsPresenter(final EventBus eventBus, final MyView view,
                                final MyProxy proxy, final PlaceManager placeManager) {
        super(eventBus, view, proxy);
        this.placeManager = placeManager;
    }

    @Override
    protected void onReset() {
        super.onReset();
        int size = placeManager.getHierarchyDepth();
        getView().clearBreadcrumbs(size);
        for (int i = 0; i < size; ++i) {
            final int index = i;
            placeManager.getTitle(i, new SetPlaceTitleHandler() {
                @Override
                public void onSetPlaceTitle(String title) {
                    getView().setBreadcrumbs(index, title);
                }
            });
        }
    }

    @Override
    protected void revealInParent() {
        RevealRootContentEvent.fire(this, this);
    }
}
