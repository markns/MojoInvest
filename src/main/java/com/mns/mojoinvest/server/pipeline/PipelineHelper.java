package com.mns.mojoinvest.server.pipeline;

import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.BlobstoreEntryRecord;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.blobstore.BlobstoreQuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.objectify.MyTypeConverters;
import com.mns.mojoinvest.server.engine.model.dao.objectify.ObjectifyEntryRecordDao;
import com.mns.mojoinvest.server.engine.model.dao.objectify.ObjectifyFundDao;
import com.sun.jersey.api.client.Client;

public class PipelineHelper {

    public static Client getClient() {
        Client client = Client.create();
        client.setFollowRedirects(true);
        client.setReadTimeout(10000);
        client.setConnectTimeout(10000);
        return client;
    }

    public static FundDao getFundDao() {
        ObjectifyFactory factory = new ObjectifyFactory();
        factory.register(Fund.class);
        factory.getConversions().add(new MyTypeConverters());
        return new ObjectifyFundDao(factory);
    }

    public static BlobstoreQuoteDao getQuoteDao() {
        ObjectifyFactory factory = new ObjectifyFactory();
        factory.register(BlobstoreEntryRecord.class);
        factory.getConversions().add(new MyTypeConverters());
        return new BlobstoreQuoteDao(new ObjectifyEntryRecordDao(factory));
    }

}
