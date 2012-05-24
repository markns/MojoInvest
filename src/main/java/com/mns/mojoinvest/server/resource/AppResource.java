package com.mns.mojoinvest.server.resource;

import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

@Path("/app")
public class AppResource {

    private final FundDao fundDao;

    @Inject
    public AppResource(FundDao fundDao) {
        this.fundDao = fundDao;
    }

    @GET
    @Produces("text/html")
    public Viewable getMytestView() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("funds", fundDao.list());
        return new Viewable("/app.mustache", map);
    }


}
