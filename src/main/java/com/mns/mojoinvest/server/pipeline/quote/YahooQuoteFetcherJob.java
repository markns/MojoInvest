package com.mns.mojoinvest.server.pipeline.quote;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.common.base.Joiner;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.ObjectifyFundDao;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

public class YahooQuoteFetcherJob extends Job1<String, LocalDate> {

    private static final Logger log = Logger.getLogger(YahooQuoteFetcherJob.class.getName());

    private static final int BATCH_SIZE = 50;

    private FundDao fundDao;

    @Override
    public Value<String> run(LocalDate date) {

        initializeFundDao();

        Collection<Fund> funds = fundDao.list();

        List<FutureValue<String>> messages = new ArrayList<FutureValue<String>>();
        List<Fund> batch = new ArrayList<Fund>(BATCH_SIZE);

        for (Fund fund : funds) {
//            if (!fund.getSymbol().equals("IJPC"))
//                continue;
            batch.add(fund);
            if (batch.size() == BATCH_SIZE) {
                List<Fund> clone = new ArrayList<Fund>(batch);
                messages.add(futureCall(new YahooQuoteFetcherBatchJob(), immediate(clone), immediate(date)));
                batch.clear();
            }
        }
        if (batch.size() > 0) {
            messages.add(futureCall(new YahooQuoteFetcherBatchJob(), immediate(batch), immediate(date)));
        }

        return futureCall(new MergeMessagesJob(), futureList(messages));
    }

    private void initializeFundDao() {
        //TODO: Figure out how to inject and serialize DAOs
        ObjectifyFactory factory = ObjectifyService.factory();
        fundDao = new ObjectifyFundDao(factory);
        fundDao.registerObjects(factory);
        //
    }

    private static class MergeMessagesJob extends Job1<String, List<String>> {

        @Override
        public Value<String> run(List<String> lists) {
            return immediate(Joiner.on("\n").join(lists));
        }
    }


}
