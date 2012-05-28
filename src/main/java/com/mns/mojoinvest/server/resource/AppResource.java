package com.mns.mojoinvest.server.resource;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.params.BacktestParams;
import com.mns.mojoinvest.server.engine.params.Params;
import com.mns.mojoinvest.server.engine.params.PortfolioParams;
import com.mns.mojoinvest.server.engine.params.StrategyParams;
import com.sun.jersey.api.view.Viewable;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.LocalDate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;

@Path("/app")
public class AppResource {

    private final FundDao fundDao;

    private final ObjectMapper mapper = new ObjectMapper();

    @Inject
    public AppResource(FundDao fundDao) {
        this.fundDao = fundDao;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getAppView() {
        Map<String, Object> map = new HashMap<String, Object>();

        Collection<Fund> funds = fundDao.list();

        BacktestParams backtestParams = getBacktestParams();
        StrategyParams strategyParams = getStrategyParams();
        PortfolioParams portfolioParams = getPortfolioParams();
        List<String> universe = getUniverse();

        Params params = new Params(backtestParams, strategyParams, portfolioParams, universe);

        try {
            map.put("funds", mapper.writeValueAsString(funds));
            map.put("params", mapper.writeValueAsString(params));
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
        return new Viewable("/app.mustache", map);
    }

    BacktestParams getBacktestParams() {
        LocalDate fDate = new LocalDate("1990-01-01");
        LocalDate tDate = new LocalDate("2012-03-01");
        Date fromDate = fDate.toDateMidnight().toDate();
        Date toDate = tDate.toDateMidnight().toDate();

        return new BacktestParams(fromDate, toDate);
    }

    public StrategyParams getStrategyParams() {
        int portfolioSize = 1;
        int holdingPeriod = 1;
        int ma1 = 12;
        int ma2 = 26;
        int roc = 26;
        int alpha = 100;
        int castOff = 8;
        boolean riskAdjust = true;
        int stddev = 26;
        boolean equityCurveTrading = true;
        int equityCurveWindow = 52;
        boolean useSafeAsset = true;
        //String safeAsset = "FSUTX"; //fidelity
        String safeAsset = "IGLT"; //ishares
        //String safeAsset = "GSPC";
        String relativeStrengthStyle = "MA";

        return new StrategyParams(portfolioSize, holdingPeriod, ma1, ma2, roc, alpha,
                castOff, riskAdjust, stddev, equityCurveTrading, equityCurveWindow,
                relativeStrengthStyle, useSafeAsset, safeAsset);
    }


    public PortfolioParams getPortfolioParams() {
        double cash = 10000d;
        double transactionCost = 10d;

        return new PortfolioParams(cash, transactionCost, new LocalDate("1990-01-01").toDateMidnight().toDate());
    }

    private List<String> getUniverse() {
        String funds = "IUSA|IEEM|IWRD|EUE|ISF|IBCX|INAA|IJPN|IFFF|IWDP|SEMB|IMEU|" +
                "BRIC|FXC|IBZL|IKOR|IEUX|MIDD|EUN|LTAM|ITWN|IEER|IPXJ|IEMS|ISP6|SSAM|SAUS|SRSA|RUSS|NFTY";
        return toList(Splitter.on("|").split(funds));
    }

    private static <E> List<E> toList(Iterable<E> iterable) {
        return (iterable instanceof List)
                ? (List<E>) iterable
                : Lists.newArrayList(iterable.iterator());
    }
}
