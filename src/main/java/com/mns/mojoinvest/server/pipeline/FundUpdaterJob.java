package com.mns.mojoinvest.server.pipeline;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.FundDao;

import java.util.List;
import java.util.logging.Logger;

public class FundUpdaterJob extends Job1<Void, List<Fund>> {

    private static final Logger log = Logger.getLogger(FundUpdaterJob.class.getName());

    @Override
    public Value<Void> run(List<Fund> funds) {

        //TODO: Figure out how to inject and serialize DAOs
        ObjectifyFactory factory = ObjectifyService.factory();
        FundDao fundDao = new FundDao(factory);
        fundDao.registerObjects(factory);
        //

        log.info("Updating " + funds.size() + " funds");
        fundDao.put(funds);

        return null;
    }


}
