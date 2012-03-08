package com.mns.mojoinvest.server.pipeline.fund;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class FundUpdaterJob extends Job1<String, List<Fund>> {

    private static final Logger log = Logger.getLogger(FundUpdaterJob.class.getName());

    @Override
    public Value<String> run(List<Fund> current) {

        //TODO: Figure out how to inject and serialize DAOs
        ObjectifyFactory factory = ObjectifyService.factory();
        FundDao dao = new FundDao(factory);
        dao.registerObjects(factory);
        //

        Set<Fund> existing = dao.list();

        log.info("Existing " + existing.size() + " - " + existing);
        log.info("Current  " + current.size() + " - " + current);

        //Subtract set of current funds from existing to find inactive.
        existing.removeAll(current);
        int currentSize = current.size();
        int existingSize = existing.size();
        for (Fund fund : existing) {
            fund.setActive(false);
        }
        current.addAll(existing);


        log.info("Setting " + existingSize + " funds as inactive: " + existing);
        log.info("Updating " + currentSize + " active funds");
        dao.put(current);

        return immediate("Set " + existingSize + " funds as inactive: " + existing + "\n" +
                "Updated " + currentSize + " active funds");
    }


}
