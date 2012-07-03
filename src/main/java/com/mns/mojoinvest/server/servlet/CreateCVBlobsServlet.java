package com.mns.mojoinvest.server.servlet;

import au.com.bytecode.opencsv.CSVReader;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

@Singleton
public class CreateCVBlobsServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(CreateCVBlobsServlet.class.getName());


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
        String s = convertStreamToString(new BlobstoreInputStream(blobKey));
        Map<String, StringBuilder> map = buildKeyToValueListMap(s);

        for (String key : map.keySet()) {
            String values = map.get(key).toString();

            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(withUrl("/blobworker")
                    .param("key", key)
                    .param("values", values));

        }

    }


    private static Map<String, StringBuilder> buildKeyToValueListMap(String s) throws IOException {
        Map<String, StringBuilder> map = new HashMap<String, StringBuilder>();
        CSVReader reader = new CSVReader(new StringReader(s));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            if (!map.containsKey(nextLine[0])) {
                map.put(nextLine[0], new StringBuilder());
            }
            map.get(nextLine[0]).append(nextLine[1]).append("\n");
        }
        return map;
    }


    private static String convertStreamToString(java.io.InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }

}
