package com.mns.mojoinvest.server.servlet.blob;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class DeleteBlobWorker extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
        blobstoreService.delete(blobKey);
    }
}
