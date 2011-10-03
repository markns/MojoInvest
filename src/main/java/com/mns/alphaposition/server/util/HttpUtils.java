package com.mns.alphaposition.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

public class HttpUtils {

    private static final Logger log = Logger.getLogger(HttpUtils.class.getName());

    public static String fetch(String url, String query) {
        String charset = "UTF-8";
        try {
            URLConnection connection = new URL(url + "?" + query).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setReadTimeout(10);
            InputStream response = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(response));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            log.severe(e.getMessage() + "\n" + e.getCause());
            throw new IllegalStateException(e);
        }
    }




}
