package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.server.params.PortfolioParams;

public interface PortfolioFactory {

    Portfolio create(PortfolioParams params, boolean shadow);

}
