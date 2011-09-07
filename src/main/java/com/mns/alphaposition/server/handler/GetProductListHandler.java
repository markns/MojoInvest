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
import com.mns.alphaposition.server.PortfolioProvider;
import com.mns.alphaposition.shared.Product;
import com.mns.alphaposition.shared.action.GetProductListAction;
import com.mns.alphaposition.shared.action.GetProductListResult;

import java.util.ArrayList;

/**
 * @author Philippe Beaudoin
 */
public class GetProductListHandler implements
        ActionHandler<GetProductListAction, GetProductListResult> {

    private final ProductDatabase database;

    private final TestStrategy strategy;

    private final PortfolioProvider portfolioProvider;

    @Inject
    public GetProductListHandler(ProductDatabase database, TestStrategy strategy,
                                 PortfolioProvider portfolioProvider) {
        this.database = database;
        this.strategy = strategy;
        this.portfolioProvider = portfolioProvider;
    }

    @Override
    public GetProductListResult execute(final GetProductListAction action,
                                        final ExecutionContext context) throws ActionException {

        portfolioProvider.setFlags(action.getFlags());
        strategy.execute();
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
