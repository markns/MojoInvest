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

package com.mns.mojoinvest.server.guice;

import com.gwtplatform.dispatch.server.guice.HandlerModule;
import com.mns.mojoinvest.server.UserInfoProvider;
import com.mns.mojoinvest.server.handler.GetFundPerformanceHandler;
import com.mns.mojoinvest.server.handler.GetProductListHandler;
import com.mns.mojoinvest.server.handler.GetUserHandler;
import com.mns.mojoinvest.server.handler.RunBackTestHandler;
import com.mns.mojoinvest.shared.action.GetProductListAction;
import com.mns.mojoinvest.shared.action.RunBackTestAction;
import com.mns.mojoinvest.shared.dispatch.GetFundPerformanceAction;
import com.mns.mojoinvest.shared.dispatch.GetUserAction;
import com.mns.mojoinvest.shared.model.UserInfo;

/**
 * Module which binds the handlers and configurations.
 *
 * @author Mark Nuttall-Smith
 */
public class ServerModule extends HandlerModule {

    @Override
    protected void configureHandlers() {

        bind(UserInfo.class).toProvider(UserInfoProvider.class);

        bindHandler(RunBackTestAction.class, RunBackTestHandler.class);
        bindHandler(GetProductListAction.class, GetProductListHandler.class);

        bindHandler(GetUserAction.class, GetUserHandler.class);
        bindHandler(GetFundPerformanceAction.class, GetFundPerformanceHandler.class);
    }
}
