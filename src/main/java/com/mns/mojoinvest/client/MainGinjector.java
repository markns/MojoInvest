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

package com.mns.mojoinvest.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.gwtplatform.dispatch.client.gin.DispatchAsyncModule;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.mns.mojoinvest.client.app.AppModule;
import com.mns.mojoinvest.client.app.AppPresenter;
import com.mns.mojoinvest.client.landing.LandingModule;
import com.mns.mojoinvest.client.landing.LandingPresenter;
import com.mns.mojoinvest.client.resources.Resources;
import com.mns.mojoinvest.client.resources.Translations;
import com.mns.mojoinvest.client.widget.WidgetModule;

@GinModules({DispatchAsyncModule.class,
        MainModule.class,
        WidgetModule.class,
        LandingModule.class,
        AppModule.class})
public interface MainGinjector extends Ginjector {

    // ------------------------------------------------------------- singletons

    EventBus getEventBus();

    PlaceManager getPlaceManager();
//    ProxyFailureHandler getProxyFailureHandler();
//    DispatchAsync getDispatcher();

    Resources getResources();

    Translations getTranslations();

    SignedInGatekeeper getSignedInGatekeeper();

    // ------------------------------------------------------------- presenters

    Provider<LandingPresenter> getLandingPresenter();

    Provider<MainPresenter> getMainPresenter();

    AsyncProvider<AppPresenter> getAppPresenter();


//    Translations getTranslations();

}