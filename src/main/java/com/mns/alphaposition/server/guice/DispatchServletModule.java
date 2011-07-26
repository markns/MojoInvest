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

package com.mns.alphaposition.server.guice;

import com.dyuproject.openid.OpenIdServletFilter;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFactory;
import com.gwtplatform.dispatch.server.guice.DispatchServiceImpl;
import com.gwtplatform.dispatch.server.guice.HttpSessionSecurityCookieFilter;
import com.gwtplatform.dispatch.shared.ActionImpl;
import com.gwtplatform.dispatch.shared.SecurityCookie;
import com.mns.alphaposition.shared.Constants;

/**
 * @author Mark Nuttall-Smith
 */
public class DispatchServletModule extends ServletModule {

    @Override
    public void configureServlets() {

        // Model object managers
        bind(ObjectifyFactory.class).in(Singleton.class);

        bindConstant().annotatedWith(SecurityCookie.class).to(Constants.securityCookieName);

        bind(OpenIdServletFilter.class).in(Singleton.class);



        // TODO philippe.beaudoin@gmail.com
        // Uncomment when http://code.google.com/p/mns/issues/detail?id=27 is unblocked.
        // filter("*").through( CrawlFilter.class );
        filter("*").through(HttpSessionSecurityCookieFilter.class);
        serve("/" + ActionImpl.DEFAULT_SERVICE_NAME).with(DispatchServiceImpl.class);
//        serve("/openid/login").with(OpenIdServlet.class);
    }

}
