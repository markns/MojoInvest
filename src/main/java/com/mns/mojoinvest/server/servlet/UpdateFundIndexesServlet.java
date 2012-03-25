package com.mns.mojoinvest.server.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.CategorySet;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.ProviderSet;
import com.mns.mojoinvest.server.engine.model.Symbols;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class UpdateFundIndexesServlet extends HttpServlet {

    private final FundDao fundDao;

    @Inject
    public UpdateFundIndexesServlet(FundDao fundDao) {
        this.fundDao = fundDao;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Collection<Fund> funds = fundDao.list();

        Set<String> providers = new HashSet<String>();
        Set<String> categories = new HashSet<String>();
        Set<String> symbols = new HashSet<String>(funds.size());
        for (Fund fund : funds) {
            providers.add(fund.getProvider());
            categories.add(fund.getCategory());
            symbols.add(fund.getSymbol());
        }

        fundDao.put(new ProviderSet(providers));
        fundDao.put(new CategorySet(categories));
        fundDao.put(new Symbols(symbols));

    }
}

