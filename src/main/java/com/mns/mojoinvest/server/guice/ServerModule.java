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
import com.mns.mojoinvest.server.handler.*;
import com.mns.mojoinvest.shared.action.BatchAction;
import com.mns.mojoinvest.shared.dispatch.*;
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

        bindHandler(BatchAction.class, BatchActionHandler.class);

        bindHandler(RunStrategyAction.class, RunStrategyHandler.class);


        bindHandler(GetUserAction.class, GetUserHandler.class);
        bindHandler(GetFundPerformanceAction.class, GetFundPerformanceHandler.class);

        bindHandler(GetPerformanceRangesAvailableAction.class, GetPerformanceRangesAvailableHandler.class);
        bindHandler(GetProvidersAvailableAction.class, GetProvidersAvailableHandler.class);
        bindHandler(GetCategoriesAvailableAction.class, GetCategoriesAvailableHandler.class);

        bindHandler(GetParamDefaultsAction.class, GetParamDefaultHandler.class);

    }
}
