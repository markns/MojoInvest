package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.shared.params.PortfolioParams;

public interface PortfolioFactory {

    Portfolio create(PortfolioParams params);

}
