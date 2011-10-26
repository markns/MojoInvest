package com.mns.alphaposition.server.pipeline;

import com.mns.alphaposition.server.engine.model.Quote;
import com.mns.alphaposition.server.yql.QueryType;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.URL;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class QuoteFetcherJobTests {

    public static final String YQL_CONTEXT = "com.mns.alphaposition.server.yql";

    private final QuoteFetcherJob job = new QuoteFetcherJob();

    @Test
    public void testRealtimeQuoteParse() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(YQL_CONTEXT);
        Unmarshaller u = jc.createUnmarshaller();
        URL url = ClassLoader.getSystemResource("etf-realtime.xml");
        @SuppressWarnings("unchecked") JAXBElement<QueryType> o = (JAXBElement<QueryType>) u.unmarshal(url);
        QueryType queryType = o.getValue();
        List<Quote> quotes = job.parseQuotes(queryType);
        assertEquals(19, quotes.size());
    }

    @Test
    public void testRealtimeQuoteParseMarketClosed() throws JAXBException {
        URL url = ClassLoader.getSystemResource("etf-realtime-market-closed.xml");
        JAXBContext jc = JAXBContext.newInstance(YQL_CONTEXT);
        Unmarshaller u = jc.createUnmarshaller();
        @SuppressWarnings("unchecked") JAXBElement<QueryType> o = (JAXBElement<QueryType>) u.unmarshal(url);
        QueryType queryType = o.getValue();
        List<Quote> quotes = job.parseQuotes(queryType);
        assertEquals(10, quotes.size());
    }




}
