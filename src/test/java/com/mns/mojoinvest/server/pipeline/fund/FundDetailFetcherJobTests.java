package com.mns.mojoinvest.server.pipeline.fund;

import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FundDetailFetcherJobTests {


    @Test
    public void scrapeDetails() throws IOException {
        String html = Files.toString(new File("src/test/resources/TNDQ.html"), Charset.defaultCharset());
        String symbol = "TDNQ";
        FundDetailFetcherJob job = new FundDetailFetcherJob();
        job.scrapeDetails(html, symbol);
    }
}
