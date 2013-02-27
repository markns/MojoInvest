package com.mns.mojoinvest.server.pipeline.fund;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static junit.framework.Assert.assertEquals;

public class ISharesFundFetcherControlJobTest {

    ISharesFundFetcherControlJob job = new ISharesFundFetcherControlJob();

    @Test
    public void testScrapeSymbols() throws IOException {
        URL url = ClassLoader.getSystemResource("ishares_product_overview.html");
        String html = FileUtils.readFileToString(new File(url.getFile()));
        assertEquals(131, job.scrapeLinks(html).size());
    }


    @Test
    public void testParseCategoryJson() throws IOException {

        URL url = ClassLoader.getSystemResource("ishares_product_overview.html");
        String html = FileUtils.readFileToString(new File(url.getFile()));

        ISharesFundFetcherControlJob job = new ISharesFundFetcherControlJob();
        job.scrapeFunds(html);


//        System.out.println(node);
    }


}

