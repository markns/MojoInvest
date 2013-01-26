package com.mns.mojoinvest.server.pipeline;

import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.BlobstoreEntryRecord;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.CalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.blobstore.BlobstoreCalculatedValueDao;
import com.mns.mojoinvest.server.engine.model.dao.blobstore.BlobstoreQuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.objectify.MyTypeConverters;
import com.mns.mojoinvest.server.engine.model.dao.objectify.ObjectifyEntryRecordDao;
import com.mns.mojoinvest.server.engine.model.dao.objectify.ObjectifyFundDao;
import com.sun.jersey.api.client.Client;

public class PipelineHelper {

    private static ObjectifyFactory factory = new ObjectifyFactory();

    static {
        factory.register(Fund.class);
        factory.register(BlobstoreEntryRecord.class);
        factory.getConversions().add(new MyTypeConverters());
    }

    public static Client getClient() {
        Client client = Client.create();
        client.setFollowRedirects(true);
        client.setReadTimeout(10000);
        client.setConnectTimeout(10000);
        return client;
    }


    //I'll just get an injector out of a singleton in the JobRecord subclasses.
    //https://groups.google.com/forum/#!topic/app-engine-pipeline-api/k4ul8XbFPoc

    public static FundDao getFundDao() {
        return new ObjectifyFundDao(factory);
    }

    public static BlobstoreQuoteDao getQuoteDao() {
        return new BlobstoreQuoteDao(new ObjectifyEntryRecordDao(factory));
    }

    public static CalculatedValueDao getCalculatedValueDao() {
        return new BlobstoreCalculatedValueDao(new ObjectifyEntryRecordDao(factory));
    }
}
