package com.mns.mojoinvest.server.pipeline.quote;


import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.pipeline.PipelineException;
import com.mns.mojoinvest.server.pipeline.PipelineHelper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ISharesQuoteFetcherJob extends Job2<String, String, String> {

    public static final String BASE_URL = "http://tools.ishares.com/tec6/download_data.do";

    @Override
    public Value<String> run(String category, String sessionId) {

        Client client = PipelineHelper.getClient();

        WebResource webResource = client.resource(BASE_URL);
        WebResource.Builder builder = webResource.getRequestBuilder();

        builder.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        builder.cookie(new Cookie("JSESSIONID", sessionId));

        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.put("action", Arrays.asList("downloadByCategory"));
        formData.put("fromDate", Arrays.asList(""));
        formData.put("toDate", Arrays.asList(""));
        formData.put("categoryCode", Arrays.asList(category));

        ClientResponse response = builder.post(ClientResponse.class, formData);

        Workbook workbook;
        try {
            workbook = Workbook.getWorkbook(response.getEntityInputStream());
            List<Quote> quotes = ISharesExcelParser.parse(workbook);
            QuoteDao dao = PipelineHelper.getQuoteDao();
            dao.put(quotes);
        } catch (IOException e) {
            throw new PipelineException("Unable to retrieve excel file " + category, e);
        } catch (BiffException e) {
            throw new PipelineException("Unable to retrieve excel file " + category, e);
        }

        return immediate(category + " quote retrieval complete");
    }


}
