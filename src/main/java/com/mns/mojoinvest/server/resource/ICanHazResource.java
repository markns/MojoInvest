package com.mns.mojoinvest.server.resource;

import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/icanhaz")
public class ICanHazResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getMytestView() {
        return new Viewable("/icanhaz.mustache");
    }


}
