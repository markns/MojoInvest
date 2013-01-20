package com.mns.mojoinvest.server.pipeline.fund;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.googlecode.objectify.NotFoundException;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.pipeline.PipelineHelper;

import java.util.*;
import java.util.logging.Logger;

public class FundUpdaterJob extends Job1<String, List<Fund>> {

    private static final Logger log = Logger.getLogger(FundUpdaterJob.class.getName());

    @Override
    public Value<String> run(List<Fund> current) {

        FundDao dao = PipelineHelper.getFundDao();

        Collection<Fund> existing;
        try {
            existing = dao.list();
        } catch (NotFoundException e) {
            existing = new HashSet<Fund>(0);
        }

        Map<String, Fund> existingMap = new HashMap<String, Fund>(existing.size());
        for (Fund fund : existing) {
            existingMap.put(fund.getSymbol(), fund);
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

        //Set properties that need to be transfered from persisted version here.
        for (Fund fund : current) {
            if (existingMap.get(fund.getSymbol()) != null) {
                fund.setLatestQuoteDate(existingMap.get(fund.getSymbol()).getLatestQuoteDate());
                fund.setEarliestQuoteDate(existingMap.get(fund.getSymbol()).getEarliestQuoteDate());
            }
        }
        dao.put(new HashSet<Fund>(current));

        return immediate(returnMessage);
    }


}
