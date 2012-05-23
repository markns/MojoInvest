package com.mns.mojoinvest.server.mustachelet;

import com.google.inject.Inject;
import com.mustachelet.annotations.Controller;
import com.mustachelet.annotations.HttpMethod;
import com.mustachelet.annotations.Path;
import com.mustachelet.annotations.Template;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;

import static com.mustachelet.annotations.HttpMethod.Type.GET;
import static com.mustachelet.annotations.HttpMethod.Type.POST;

/**
 * Post / redirect handling
 * <p/>
 * User: sam
 * Date: 12/21/10
 * Time: 3:49 PM
 */
@Path("/m/post(/(.*))?")
@Template("/post.mustache")
@HttpMethod({GET, POST})
public class Post {
    @Inject
    HttpServletResponse response;

    @Inject
    HttpServletRequest request;

    @Controller(POST)
    boolean redirectPostData() throws IOException {
        response.sendRedirect("/m/post/" + request.getParameter("value"));
        return false;
    }

    @Inject
    Matcher m;

    String value() {
        return m.group(2);
    }
}
