package com.mns.mojoinvest.server.mustachelet;

import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mustachelet.annotations.Controller;
import com.mustachelet.annotations.Path;
import com.mustachelet.annotations.Template;

import java.util.Collection;

@Path("/app")
@Template("app.mustache")
public class App {

    private FundDao fundDao;

    @Inject
    public App(FundDao fundDao) {
        this.fundDao = fundDao;
    }

    @Controller
    boolean exists() {
        return true;
    }

    public Collection<Fund> funds() {
        return fundDao.list();
    }

}
