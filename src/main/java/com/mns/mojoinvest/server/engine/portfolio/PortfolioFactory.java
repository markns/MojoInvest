package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.server.engine.params.Params;

public interface PortfolioFactory {

    Portfolio create(Params params, boolean shadow);

}
