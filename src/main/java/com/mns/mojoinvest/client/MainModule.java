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

package com.mns.mojoinvest.client;

import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.mns.mojoinvest.client.resources.Resources;
import com.mns.mojoinvest.client.resources.Translations;

public class MainModule extends AbstractPresenterModule {

    @Override
    protected void configure() {
        // Default implementation of standard resources
        install(new DefaultModule(MainPlaceManager.class));


        bind(Resources.class).in(Singleton.class);
        bind(Translations.class).in(Singleton.class);
        bind(ClientState.class).in(Singleton.class);
        // bind(SignedInGatekeeper.class).in(Singleton.class);


        bindPresenter(MainPresenter.class, MainPresenter.MyView.class,
                MainView.class, MainPresenter.MyProxy.class);

    }
}