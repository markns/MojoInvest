package com.mns.mojoinvest.server.servlet.blob;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

@Singleton
public class DeleteBlobs extends HttpServlet {

    private static final Logger log = Logger.getLogger(DeleteBlobs.class.getName());


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        log.info("Delete all blobs called");
        Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfos();
        Queue queue = QueueFactory.getDefaultQueue();
        while (iterator.hasNext()) {
            queue.add(withUrl("/tools/deleteblobworker")
                    .param("blob-key", iterator.next().getBlobKey().getKeyString()));
        }

    }
}
