package com.mns.mojoinvest.server.pipeline.fund;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static junit.framework.Assert.assertEquals;

public class ISharesFundFetcherJobTest {

    ISharesFundFetcherJob job = new ISharesFundFetcherJob();

    @Test
    public void testScrapeSymbols() throws IOException {
        URL url = ClassLoader.getSystemResource("ishares_product_overview.html");
        String html = FileUtils.readFileToString(new File(url.getFile()));
        assertEquals(131, job.scrapeSymbols(html).size());
    }


    @Test
    public void testParseCategoryJson() throws IOException {

        URL url = ClassLoader.getSystemResource("categories.json");
        String json = FileUtils.readFileToString(new File(url.getFile()));


//        System.out.println(node);
    }


}

