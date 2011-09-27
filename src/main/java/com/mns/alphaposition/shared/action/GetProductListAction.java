package com.mns.alphaposition.shared.action;

import com.gwtplatform.dispatch.shared.Action;
import com.mns.alphaposition.shared.params.*;

import java.math.BigDecimal;

public class GetProductListAction implements Action<GetProductListResult> {

    int flags;

    private PortfolioParams portfolioParams = new PortfolioParams(new BigDecimal("10000"), BigDecimal.TEN);
    RankingStrategyParams rankingParams = new SimpleRankingStrategyParams(1, 9);
    private StrategyParams strategyParams = new MomentumStrategyParams(1, rankingParams, 3);

    public GetProductListAction(int flags) {
        this.flags = flags;
    }

    protected GetProductListAction() {
        // Possibly for serialization.
    }

    public int getFlags() {
        return flags;
    }

    @Override
    public String getServiceName() {
        return Action.DEFAULT_SERVICE_NAME + "GetProductList";
    }

    @Override
    public boolean isSecured() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GetProductListAction other = (GetProductListAction) obj;
        if (flags != other.flags)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 23;
        hashCode = (hashCode * 37) + new Integer(flags).hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "GetProductListAction["
                + flags
                + "]";
    }

    public PortfolioParams getPortfolioParams() {
        return portfolioParams;
    }

    public StrategyParams getStrategyParams() {
        return strategyParams;
    }
}
