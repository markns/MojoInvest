package com.mns.mojoinvest.server.resource;

import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/tools")
public class ToolsResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getMytestView() {
        Map<String, Object> map = new HashMap<String, Object>();
        return new Viewable("/tools.mustache", map);
    }

}
