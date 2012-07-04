package com.mns.mojoinvest.server.servlet;

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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

@Singleton
public class CreateQuoteBlobsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
        String s = convertStreamToString(new BlobstoreInputStream(blobKey));
        Map<String, StringBuilder> map = buildKeyToValueListMap(s);

        for (String key : map.keySet()) {
            String value = map.get(key).toString();
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(withUrl("/blobworker")
                    .param("key", key)
                    .param("value", value));
        }
    }


    private static Map<String, StringBuilder> buildKeyToValueListMap(String s) throws IOException {
        Map<String, StringBuilder> map = new HashMap<String, StringBuilder>();
        Pattern pattern = Pattern.compile("^\"(\\w+)\",\"(\\d{4})-");
        for (String line : s.split("\n")) {
            Matcher matcher = pattern.matcher(line);
            matcher.find();
            String symbol = matcher.group(1);
            String year = matcher.group(2);
            String key = symbol + "|" + year;
            if (!map.containsKey(key)) {
                map.put(key, new StringBuilder());
            }
            map.get(key).append(line).append("\n");
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
