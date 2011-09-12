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
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;
import com.mns.alphaposition.server.engine.portfolio.Portfolio;
import com.mns.alphaposition.server.engine.portfolio.PortfolioFactory;
import com.mns.alphaposition.server.engine.portfolio.PortfolioProvider;
import com.mns.alphaposition.server.engine.strategy.TradingStrategy;
import com.mns.alphaposition.shared.Product;
import com.mns.alphaposition.shared.action.GetProductListAction;
import com.mns.alphaposition.shared.action.GetProductListResult;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author Philippe Beaudoin
 */
public class GetProductListHandler implements
        ActionHandler<GetProductListAction, GetProductListResult> {

    private final ProductDatabase database;

    private final PortfolioProvider portfolioProvider;
    private final PortfolioFactory portfolioFactory;

    private final Set<TradingStrategy> strategies;

    @Inject
    public GetProductListHandler(PortfolioFactory portfolioFactory,
                                 ProductDatabase database,
                                 Set<TradingStrategy> strategies,
                                 PortfolioProvider portfolioProvider) {
        this.portfolioFactory = portfolioFactory;
        this.database = database;
        this.strategies = strategies;
        this.portfolioProvider = portfolioProvider;
    }

    @Override
    public GetProductListResult execute(final GetProductListAction action,
                                        final ExecutionContext context) throws ActionException {
        Portfolio portfolio = portfolioFactory.create(action.getPortfolioParams());
        portfolioProvider.setPortfolio(portfolio);
        for (TradingStrategy strategy : strategies) {
            if (strategy.supports(action.getStrategyParams())) {
                strategy.execute(action);
            }
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
