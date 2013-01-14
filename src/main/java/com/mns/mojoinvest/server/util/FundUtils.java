package com.mns.mojoinvest.server.util;

import com.mns.mojoinvest.server.engine.model.Fund;

import java.util.*;

public class FundUtils {

    public static Map<String, Fund> getSymbolToFundMap(Collection<Fund> funds) {
        Map<String, Fund> symbolToFund = new HashMap<String, Fund>();
        for (Fund fund : funds) {
            if (!symbolToFund.containsKey(fund.getSymbol()))
                symbolToFund.put(fund.getSymbol(), fund);
        }
        return symbolToFund;
    }


    public static List<String> symbols(List<Fund> funds) {
        List<String> symbols = new ArrayList<String>(funds.size());
        for (Fund fund : funds) {
            symbols.add(fund.getSymbol());
        }
        return symbols;
    }

}
