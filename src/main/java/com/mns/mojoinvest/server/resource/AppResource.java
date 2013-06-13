package com.mns.mojoinvest.server.resource;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.params.Params;
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

        Params params = getParams();

        List<String> universe = new ArrayList<String>();
        for (Fund fund : funds) {
            if (fund.getCategory().contains("Equity"))
                universe.add(fund.getSymbol());
        }
        params.setUniverse(universe);

        try {
            map.put("funds", mapper.writeValueAsString(funds));
            map.put("params", mapper.writeValueAsString(params));
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
        return new Viewable("/app.mustache", map);
    }

    public Params getParams() {

        LocalDate fromDate = new LocalDate("2005-01-01");
        LocalDate toDate = new LocalDate();

        int portfolioSize = 2;
        int holdingPeriod = 1;
        int minHoldingPeriod = 6;
        int ma1 = 12;
        int ma2 = 26;
        int roc = 26;
        int alpha = 100;
        int castOff = 5;
        boolean riskAdjust = true;
        int stddev = 8;
        boolean equityCurveTrading = true;
        int equityCurveWindow = 26;
        boolean useSafeAsset = true;
        //String safeAsset = "FSUTX"; //fidelity
        String safeAsset = "INXG"; //ishares
        //String safeAsset = "GSPC";
        String relativeStrengthStyle = "ROC";

        double initialInvestment = 100000d;
        double transactionCost = 10d;
        LocalDate creationDate = new LocalDate("1990-01-01");

        boolean useCorrelationFilter = true;
        double correlationThreshold = 0.98;

        return new Params(fromDate, toDate, creationDate, initialInvestment, transactionCost,
                portfolioSize, holdingPeriod, minHoldingPeriod, ma1, ma2, roc,
                alpha, castOff, riskAdjust, stddev, equityCurveTrading,
                equityCurveWindow, relativeStrengthStyle, useSafeAsset, safeAsset,
                null,
//                new ArrayList<String>(),
                useCorrelationFilter, correlationThreshold);
    }


    private List<String> getUniverse() {
        String funds = "ISF|BRIC|FXC|IBZL|IEEM|IEER|IEMI|IEMS|IFFF|IGCC|IKOR|ISEM|ITKY|ITWN|LTAM|NFTY|RUSS";
        return toList(Splitter.on("|").split(funds));
    }

    private static <E> List<E> toList(Iterable<E> iterable) {
        return (iterable instanceof List)
                ? (List<E>) iterable
                : Lists.newArrayList(iterable.iterator());
    }
}
