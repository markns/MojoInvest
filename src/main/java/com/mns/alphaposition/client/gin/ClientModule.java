/**
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.mns.alphaposition.client.gin;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.mns.alphaposition.client.AlphapositionPlaceManager;
import com.mns.alphaposition.client.NameTokens;
import com.mns.alphaposition.client.presenter.BreadcrumbsPresenter;
import com.mns.alphaposition.client.presenter.HomePresenter;
import com.mns.alphaposition.client.presenter.ProductListPresenter;
import com.mns.alphaposition.client.view.BreadcrumbsView;
import com.mns.alphaposition.client.view.HomeView;
import com.mns.alphaposition.client.view.ProductListView;

/**
 * @author Christian Goudreau
 */
public class ClientModule extends AbstractPresenterModule {
  @Override
  protected void configure() {
    // Default implementation of standard resources
    install(new DefaultModule(AlphapositionPlaceManager.class));

    // Constants
    bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.homePage);

    // Presenters
    bindPresenter(BreadcrumbsPresenter.class,
        BreadcrumbsPresenter.MyView.class, BreadcrumbsView.class,
        BreadcrumbsPresenter.MyProxy.class);
    bindPresenter(HomePresenter.class, HomePresenter.MyView.class,
        HomeView.class, HomePresenter.MyProxy.class);
    bindPresenter(ProductListPresenter.class,
        ProductListPresenter.MyView.class, ProductListView.class,
        ProductListPresenter.MyProxy.class);
//    bindPresenter(ProductPresenter.class, ProductPresenter.MyView.class,
//        ProductView.class, ProductPresenter.MyProxy.class);
  }
}