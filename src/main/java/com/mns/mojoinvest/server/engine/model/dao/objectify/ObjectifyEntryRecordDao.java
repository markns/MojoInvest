package com.mns.mojoinvest.server.engine.model.dao.objectify;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.BlobstoreEntryRecord;

import java.util.Map;

public class ObjectifyEntryRecordDao extends DAOBase {

    private static boolean objectsRegistered;

    @Inject
    public ObjectifyEntryRecordDao(final ObjectifyFactory objectifyFactory) {
        super(objectifyFactory);
    }

    @Override
    protected boolean areObjectsRegistered() {
        return objectsRegistered;
    }

    @Override
    public void registerObjects(ObjectifyFactory ofyFactory) {
        if (objectsRegistered)
            return;

        objectsRegistered = true;
        ofyFactory.register(BlobstoreEntryRecord.class);
    }

    public Key<BlobstoreEntryRecord> put(BlobstoreEntryRecord record) {
        return ofy().put(record);
    }

    public Map<Key<BlobstoreEntryRecord>, BlobstoreEntryRecord> put(Iterable<BlobstoreEntryRecord> records) {
        return ofy().put(records);
    }

    public BlobstoreEntryRecord get(String keyStr) {
        try {
            Key<BlobstoreEntryRecord> key = new Key<BlobstoreEntryRecord>(BlobstoreEntryRecord.class, keyStr);
            return ofy().get(key);
        } catch (NotFoundException e) {
            return null;
        }
    }

    public void delete(BlobstoreEntryRecord record) {
        ofy().delete(record);
    }
}
