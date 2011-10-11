/* Copyright (c) 2009 Google Inc.
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

package com.mns.alphaposition.server.servlet;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.inject.Singleton;
import com.mns.alphaposition.server.servlet.util.ParameterNotFoundException;
import com.mns.alphaposition.server.servlet.util.ParameterParser;
import com.mns.alphaposition.server.util.TradingDayUtils;
import org.joda.time.LocalDate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;


/**
 * Servlet that schedules a counter to be increased.
 */
@Singleton
public class PerformanceRankingServlet extends HttpServlet {


    private static final Logger log = Logger.getLogger(PerformanceRankingServlet.class.getName());


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

                ParameterParser parser = new ParameterParser(req);

                try {
                    LocalDate fromDate = parser.getLocalDateParameter("fromDate");
                    LocalDate toDate = parser.getLocalDateParameter("toDate");
            int range = parser.getIntParameter("range");

            List<LocalDate> dates = TradingDayUtils.getDailySeries(fromDate, toDate, true);

            Queue queue = QueueFactory.getDefaultQueue();

            for (LocalDate date : dates) {
                queue.add(withUrl("/workers/ranker")
                                .param("date", date.toString())
                                .param("range", range + ""));
            }

            resp.sendRedirect("/ranker.jsp");

        } catch (ParameterNotFoundException e) {
            throw new IOException(e);
        }

    }
}
