package com.mns.mojoinvest.server.engine.model;

import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.Id;

@Cached
public class BlobstoreKeyRecord {

    @Id
    private String key;

    @Unindexed
    private BlobKey blobKey;

    public BlobstoreKeyRecord(String key, BlobKey blobKey) {
        this.key = key;
        this.blobKey = blobKey;
    }

    public BlobstoreKeyRecord() {
        //For Objectify
    }

    public String getKey() {
        return key;
    }

    public BlobKey getBlobKey() {
        return blobKey;
    }
}
