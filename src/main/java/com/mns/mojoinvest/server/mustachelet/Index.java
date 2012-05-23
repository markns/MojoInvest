package com.mns.mojoinvest.server.mustachelet;

import com.mustachelet.annotations.Controller;
import com.mustachelet.annotations.Path;
import com.mustachelet.annotations.Template;

/**
 * Index page.
 * <p/>
 * User: sam
 * Date: 12/21/10
 * Time: 2:22 PM
 */
@Path("/")
@Template("index.mustache")
public class Index {

    @Controller
    boolean exists() {
        return true;
    }

    String name() {
        return "Sam";
    }
}
