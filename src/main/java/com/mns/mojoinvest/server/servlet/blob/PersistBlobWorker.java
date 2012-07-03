package com.mns.mojoinvest.server.servlet.blob;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mns.mojoinvest.server.engine.model.BlobstoreKeyRecord;
import com.mns.mojoinvest.server.engine.model.dao.BlobstoreKeyRecordDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;

@Singleton
public class PersistBlobWorker extends HttpServlet {

    // Get a file service
    private FileService fileService = FileServiceFactory.getFileService();

    private BlobstoreKeyRecordDao dao;

    @Inject
    public PersistBlobWorker(BlobstoreKeyRecordDao dao) {
        this.dao = dao;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String key = req.getParameter("key");
        String values = req.getParameter("values");

        AppEngineFile file = writeValuesToBlob(key, values);
        writeBlobstoreKeyRecord(key, file);

    }

    private void writeBlobstoreKeyRecord(String key, AppEngineFile file) {
        // Now read from the file using the Blobstore API
        BlobKey blobKey2 = fileService.getBlobKey(file);
        BlobstoreKeyRecord record = new BlobstoreKeyRecord(key, blobKey2);
        dao.put(record);
    }

    private AppEngineFile writeValuesToBlob(String key, String values) throws IOException {
        // Create a new Blob file with mime-type "text/plain"
        AppEngineFile file = fileService.createNewBlobFile("text/plain", key);

        // Open a channel to write to it
        boolean lock = true;
        FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

        // Different standard Java ways of writing to the channel
        // are possible. Here we use a PrintWriter:
        PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
        out.println(values);

        // Close without finalizing and save the file path for writing later
        out.close();

        // Now finalize
        writeChannel.closeFinally();
        return file;
    }
}
