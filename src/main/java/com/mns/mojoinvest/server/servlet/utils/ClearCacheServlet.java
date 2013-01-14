package com.mns.mojoinvest.server.servlet.utils;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Singleton;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class ClearCacheServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        MemcacheService memcache = MemcacheServiceFactory.getMemcacheService("ObjectifyCache");
        memcache.clearAll();
        resp.setContentType("text/html");
        resp.getWriter().write("<p>Done</p>");
    }
}
