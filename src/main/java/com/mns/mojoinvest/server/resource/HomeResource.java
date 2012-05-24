package com.mns.mojoinvest.server.resource;

import com.sun.jersey.api.view.Viewable;
import org.joda.time.DateTime;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/home")
public class HomeResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getMytestView() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", new DateTime());
        return new Viewable("/home.mustache", map);
    }


}