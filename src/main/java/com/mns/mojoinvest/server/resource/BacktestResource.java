package com.mns.mojoinvest.server.resource;

import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.params.Params;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/backtest")
public class BacktestResource {

    private final FundDao fundDao;

    private final ObjectMapper mapper = new ObjectMapper();

    @Inject
    public BacktestResource(final FundDao fundDao) {
        this.fundDao = fundDao;
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public void runBacktest(Params params) {
        System.out.println(params);
    }

}
