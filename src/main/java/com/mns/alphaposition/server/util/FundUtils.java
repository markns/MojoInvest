package com.mns.alphaposition.server.util;

import com.mns.alphaposition.server.engine.model.Fund;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FundUtils {

    public static Map<String, Fund> getSymbolToFundMap(Collection<Fund> funds) {
        Map<String, Fund> symbolToFund = new HashMap<String, Fund>();
        for (Fund fund : funds) {
            if (!symbolToFund.containsKey(fund.getSymbol()))
                symbolToFund.put(fund.getSymbol(),fund);
        }
        return symbolToFund;
    }

}
