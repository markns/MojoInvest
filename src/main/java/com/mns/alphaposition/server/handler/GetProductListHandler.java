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

package com.mns.alphaposition.server.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.alphaposition.server.engine.model.Fund;
import com.mns.alphaposition.server.engine.model.FundDao;
import com.mns.alphaposition.server.engine.portfolio.Portfolio;
import com.mns.alphaposition.server.engine.portfolio.PortfolioFactory;
import com.mns.alphaposition.server.engine.portfolio.PortfolioProvider;
import com.mns.alphaposition.server.engine.strategy.StrategyException;
import com.mns.alphaposition.server.engine.strategy.TradingStrategy;
import com.mns.alphaposition.shared.Product;
import com.mns.alphaposition.shared.action.GetProductListAction;
import com.mns.alphaposition.shared.action.GetProductListResult;
import org.joda.time.LocalDate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetProductListHandler implements
        ActionHandler<GetProductListAction, GetProductListResult> {

    private final ProductDatabase database;

    private final PortfolioProvider portfolioProvider;
    private final PortfolioFactory portfolioFactory;

    private final FundDao fundDao;

    private final Map<Class, TradingStrategy> strategies;

    @Inject
    Provider<HttpServletRequest> requestProvider;


    @Inject
    public GetProductListHandler(PortfolioFactory portfolioFactory,
                                 PortfolioProvider portfolioProvider,
                                 Map<Class, TradingStrategy> strategies,
                                 FundDao fundDao,
                                 ProductDatabase database) {
        this.portfolioFactory = portfolioFactory;
        this.portfolioProvider = portfolioProvider;
        this.database = database;
        this.fundDao = fundDao;
        this.strategies = strategies;
    }

    @Override
    public GetProductListResult execute(final GetProductListAction action,
                                        final ExecutionContext context) throws ActionException {

        Portfolio portfolio = portfolioFactory.create(action.getPortfolioParams());
        HttpServletRequest request = requestProvider.get();
        request.setAttribute("portfolio", portfolio);

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("provider", "iShares");
        List<Fund> funds = fundDao.query(queryParams);
//        List<Fund> funds = fundDao.list();

        try {
            TradingStrategy strategy = strategies.get(action.getStrategyParams().getClass());
            strategy.execute(new LocalDate(2000, 1, 1), new LocalDate(2011, 9, 16),
                    funds, action.getStrategyParams());
        } catch (StrategyException e) {
            throw new ActionException();
        }

        ArrayList<Product> products = database.getMatching(action.getFlags());
        return new GetProductListResult(products);
    }

    @Override
    public Class<GetProductListAction> getActionType() {
        return GetProductListAction.class;
    }

    @Override
    public void undo(final GetProductListAction action,
                     final GetProductListResult result, final ExecutionContext context)
            throws ActionException {
        // No undo
    }
}
