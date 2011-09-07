/**
 * Copyright 2010 Mark Nuttall-Smith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mns.alphaposition.client.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.gwtplatform.dispatch.client.gin.DispatchAsyncModule;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.mns.alphaposition.client.presenter.BreadcrumbsPresenter;
import com.mns.alphaposition.client.presenter.HomePresenter;
import com.mns.alphaposition.client.presenter.ProductListPresenter;

@GinModules({DispatchAsyncModule.class, ClientModule.class})
public interface ClientGinjector extends Ginjector {

    EventBus getEventBus();

    AsyncProvider<HomePresenter> getHomePresenter();

    Provider<BreadcrumbsPresenter> getBreadcrumbsPresenter();

    PlaceManager getPlaceManager();

//    Resources getResources();

    AsyncProvider<ProductListPresenter> getProductListPresenter();

    DispatchAsync getDispatcher();

//    Translations getTranslations();

}