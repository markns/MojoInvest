package com.mns.mojoinvest.server.resource;

import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.params.BacktestParams;
import com.sun.jersey.api.view.Viewable;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.LocalDate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

        BacktestParams params = getBacktestParams();

        try {
            map.put("funds", mapper.writeValueAsString(funds));
            map.put("backtestParams", mapper.writeValueAsString(params));
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


}
