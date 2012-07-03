package com.mns.mojoinvest.server.engine.model.dao;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.BlobstoreKeyRecord;

import java.util.Map;

public class BlobstoreKeyRecordDao extends DAOBase {

    private static boolean objectsRegistered;

    @Inject
    public BlobstoreKeyRecordDao(final ObjectifyFactory objectifyFactory) {
        super(objectifyFactory);
    }

    @Override
    protected boolean areObjectsRegistered() {
        return objectsRegistered;
    }

    @Override
    public void registerObjects(ObjectifyFactory ofyFactory) {
        objectsRegistered = true;
        ofyFactory.register(BlobstoreKeyRecord.class);
    }

    public Key<BlobstoreKeyRecord> put(BlobstoreKeyRecord record) {
        return ofy().put(record);
    }

    public Map<Key<BlobstoreKeyRecord>, BlobstoreKeyRecord> put(Iterable<BlobstoreKeyRecord> records) {
        return ofy().put(records);
    }

    public BlobstoreKeyRecord get(String keyStr) {
        Key<BlobstoreKeyRecord> key = new Key<BlobstoreKeyRecord>(BlobstoreKeyRecord.class, keyStr);
        return ofy().get(key);
    }

}
