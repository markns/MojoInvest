package com.mns.mojoinvest.server.pipeline.fund;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class FundFetcherJobTests {

    @Test
    public void testListScrape() throws IOException {
        URL url = ClassLoader.getSystemResource("etfperformancetracker.html");
        String html = FileUtils.readFileToString(new File(url.getFile()));
        FundFetcherJob job = new FundFetcherJob();
        List<String> symbols = job.scrapeSymbols(html);
        assertEquals(20, symbols.size());
    }

}
