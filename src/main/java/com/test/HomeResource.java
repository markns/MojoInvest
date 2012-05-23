package com.test;

import com.sun.jersey.api.view.Viewable;
import org.joda.time.DateTime;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

@Path("/home")
public class HomeResource {

    @GET
    @Produces("text/html")
    public Viewable getMytestView() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", new DateTime());
        return new Viewable("/home.mustache", map);
    }


}