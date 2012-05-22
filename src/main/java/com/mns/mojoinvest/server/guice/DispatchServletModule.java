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
import com.mns.mojoinvest.server.engine.model.dao.*;
import com.mns.mojoinvest.server.servlet.*;
import com.mustachelet.MustacheletService;

/**
 * @author Mark Nuttall-Smith
 */
public class DispatchServletModule extends ServletModule {

    @Override
    public void configureServlets() {

        // Model object managers
        bind(ObjectifyFactory.class).in(Singleton.class);

        bind(QuoteDao.class).to(ObjectifyQuoteDao.class);
        bind(FundDao.class).to(ObjectifyFundDao.class);
        bind(CalculatedValueDao.class).to(ObjectifyCalculatedValueDao.class);

        serve("/mapreduce/*").with(InjectingMapReduceServlet.class);
        bind(InjectingMapReduceServlet.class).in(Singleton.class);

        serve("/pipeline").with(PipelineServlet.class);
        serve("/quoteviewer").with(QuoteViewerServlet.class);
        serve("/fundviewer").with(FundViewerServlet.class);
        serve("/rankingviewer").with(RankingViewerServlet.class);
        serve("/calculator").with(SMACalculatorServlet.class);
        serve("/clearcache").with(ClearCacheServlet.class);
        serve("/fundindexes").with(UpdateFundIndexesServlet.class);
        serve("/test2").with(Test2Servlet.class);

        serve("/").with(MustacheletService.class);

    }

}
