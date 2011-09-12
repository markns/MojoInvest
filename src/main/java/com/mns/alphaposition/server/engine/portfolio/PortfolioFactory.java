package com.mns.alphaposition.server.engine.portfolio;


import com.mns.alphaposition.shared.params.PortfolioParams;

public interface PortfolioFactory {

    Portfolio create(PortfolioParams params);

}
