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

import com.google.appengine.tools.mapreduce.InjectingMapReduceServlet;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFactory;
import com.gwtplatform.crawler.server.CrawlFilter;
import com.gwtplatform.crawler.server.ServiceKey;
import com.gwtplatform.crawler.server.ServiceUrl;
import com.gwtplatform.dispatch.server.guice.DispatchServiceImpl;
import com.gwtplatform.dispatch.shared.ActionImpl;
import com.mns.mojoinvest.server.servlet.*;
import com.mns.mojoinvest.test.SimpleBoatServlet;

/**
 * @author Mark Nuttall-Smith
 */
public class DispatchServletModule extends ServletModule {

    @Override
    public void configureServlets() {

        // Model object managers
        bind(ObjectifyFactory.class).in(Singleton.class);

        serve("/mapreduce/*").with(InjectingMapReduceServlet.class);
        bind(InjectingMapReduceServlet.class).in(Singleton.class);


        serve("/pipeline").with(PipelineServlet.class);
        serve("/quoteviewer").with(QuoteViewerServlet.class);
        serve("/fundviewer").with(FundViewerServlet.class);
        serve("/rankingviewer").with(RankingViewerServlet.class);
        serve("/strategy").with(StrategyServlet.class);
        serve("/calculator").with(SMACalculatorServlet.class);
        serve("/clearcache").with(ClearCacheServlet.class);
        serve("/fundindexes").with(UpdateFundIndexesServlet.class);
        serve("/test2").with(Test2Servlet.class);
        serve("/test").with(SimpleBoatServlet.class);

        bindConstant().annotatedWith(ServiceKey.class).to("123456");
        bindConstant().annotatedWith(ServiceUrl.class).to("http://crawlservice.appspot.com/");
        filter("/*").through(CrawlFilter.class);


        serve("/" + ActionImpl.DEFAULT_SERVICE_NAME + "*").with(
                DispatchServiceImpl.class);
    }

}
