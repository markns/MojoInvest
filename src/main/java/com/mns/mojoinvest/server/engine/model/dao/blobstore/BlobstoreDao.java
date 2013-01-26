package com.mns.mojoinvest.server.engine.model.dao.blobstore;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.mns.mojoinvest.server.engine.model.BlobstoreEntryRecord;
import com.mns.mojoinvest.server.engine.model.dao.objectify.ObjectifyEntryRecordDao;

import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.Collection;

public class BlobstoreDao {

    protected final ObjectifyEntryRecordDao recordDao;
    protected final FileService fileService = FileServiceFactory.getFileService();
    protected final BlobstoreService blobService = BlobstoreServiceFactory.getBlobstoreService();

    public BlobstoreDao(ObjectifyEntryRecordDao recordDao) {
        this.recordDao = recordDao;
    }

    protected void writeBlobstoreKeyRecord(String key, AppEngineFile file) {
        // Now read from the file using the Blobstore API
        BlobKey blobKey = fileService.getBlobKey(file);
        BlobstoreEntryRecord record = new BlobstoreEntryRecord(key, blobKey);
        recordDao.put(record);
    }

    protected AppEngineFile writeValuesToBlob(String key, Collection<String> values) throws Exception {
        // Create a new Blob file with mime-type "text/plain"
        AppEngineFile file = fileService.createNewBlobFile("text/plain", key);

        // Open a channel to write to it
        boolean lock = true;
        FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

        // Different standard Java ways of writing to the channel
        // are possible. Here we use a PrintWriter:
        PrintWriter writer = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
        for (String value : values) {
            writer.println(value);
        }

        // Close without finalizing and save the file path for writing later
        writer.close();

        // Now finalize
        writeChannel.closeFinally();
        return file;
    }
}
