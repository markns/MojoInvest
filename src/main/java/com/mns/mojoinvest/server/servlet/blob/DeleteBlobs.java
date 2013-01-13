package com.mns.mojoinvest.server.servlet.blob;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

@Singleton
public class DeleteBlobs extends HttpServlet {

    private static final Logger log = Logger.getLogger(DeleteBlobs.class.getName());


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        log.info("Delete all blobs called");
        List<BlobInfo> blobsToDelete = new LinkedList<BlobInfo>();
        Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfos();
        while (iterator.hasNext())
            blobsToDelete.add(iterator.next());

        log.info("Found " + blobsToDelete.size() + " blobs to delete");

        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        for (BlobInfo blobInfo : blobsToDelete)
            blobstoreService.delete(blobInfo.getBlobKey());
    }
}
