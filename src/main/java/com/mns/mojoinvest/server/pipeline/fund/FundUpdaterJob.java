package com.mns.mojoinvest.server.pipeline.fund;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.ObjectifyFundDao;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class FundUpdaterJob extends Job1<String, List<Fund>> {

    private static final Logger log = Logger.getLogger(FundUpdaterJob.class.getName());

    @Override
    public Value<String> run(List<Fund> current) {

        //TODO: Figure out how to inject and serialize DAOs
        ObjectifyFactory factory = ObjectifyService.factory();
        FundDao dao = new ObjectifyFundDao(factory);
        dao.registerObjects(factory);
        //

        Collection<Fund> existing = null;
        try {
            existing = dao.getAll();
        } catch (NotFoundException e) {
            existing = new HashSet<Fund>(0);
        }

        //Subtract set of current funds from existing to find inactive.
        existing.removeAll(current);
        for (Fund fund : existing) {
            fund.setActive(false);
        }
        String message = "Setting " + existing.size() + " funds as inactive: " + existing;
        log.info(message);
        String returnMessage = "" + message;
        dao.put(new HashSet<Fund>(existing));

        message = "Updating " + current.size() + " active funds";
        log.info(message);
        returnMessage = returnMessage + "\n" + message;

        dao.put(new HashSet<Fund>(current));

        return immediate(returnMessage);
    }
}
