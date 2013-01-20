package com.mns.mojoinvest.server.pipeline.quote;


import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.pipeline.PipelineException;
import com.mns.mojoinvest.server.pipeline.PipelineHelper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.joda.time.LocalDate;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ISharesQuoteFetcherJob extends Job2<String, String, String> {

    public static final String BASE_URL = "http://tools.ishares.com/tec6/download_data.do";

    @Override
    public Value<String> run(String category, String sessionId) {

        ClientResponse response = downloadISharesData(category, sessionId);

        Workbook workbook;
        try {
            workbook = Workbook.getWorkbook(response.getEntityInputStream());
            List<Quote> quotes = ISharesExcelParser.parse(workbook);
            QuoteDao dao = PipelineHelper.getQuoteDao();

            //Todo: Test for changes
            if (testForChanges(quotes)) {
                //Todo: set property requiring all cvs be recalculated
            }
            dao.put(quotes);
            updateLatestQuoteDates(quotes);

        } catch (IOException e) {
            throw new PipelineException("Unable to retrieve excel file " + category, e);
        } catch (BiffException e) {
            throw new PipelineException("Unable to retrieve excel file " + category, e);
        }

        return immediate(category + " quote retrieval complete");
    }

    private ClientResponse downloadISharesData(String category, String sessionId) {
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

        return builder.post(ClientResponse.class, formData);
    }

    private static void updateLatestQuoteDates(List<Quote> quotes) {

        Map<String, LocalDate> latestQuotes = new HashMap<String, LocalDate>();
        for (Quote quote : quotes) {
            if (latestQuotes.get(quote.getSymbol()) == null) {
                latestQuotes.put(quote.getSymbol(), quote.getDate());
            }
            if (quote.getDate().isAfter(latestQuotes.get(quote.getSymbol()))) {
                latestQuotes.put(quote.getSymbol(), quote.getDate());
            }
        }

        FundDao dao = PipelineHelper.getFundDao();
        for (Map.Entry<String, LocalDate> latestQuote : latestQuotes.entrySet()) {
            Fund fund = dao.get(latestQuote.getKey());
            fund.setLatestQuoteDate(latestQuote.getValue());
            dao.put(fund);
        }
    }


    private boolean testForChanges(List<Quote> quotes) {

//        Map<LocalDate, Quote> existingMap = new HashMap<LocalDate, Quote>();
//        for (Quote quote : quotes) {
//            try {
//                Quote existing = quoteDao.get(quote.getSymbol(), quote.getDate());
//                existingMap.put(existing.getDate(), existing);
//            } catch (QuoteUnavailableException e) { /**/ }
//        }
//
//        for (Quote quote : quotes) {
//            Quote existing = existingMap.get(quote.getDate());
//            if (existing == null) continue;
//            if (!quote.equals(existing)) {
//                String message;
//                messages.add(immediate(message = "New " + quote.getSymbol() + " quotes don't match existing quotes, redownloading\n" +
//                        "Existing: " + existing.toDescriptiveString() +
//                        "\n     New: " + quote.toDescriptiveString()));
//                log.warning(message);
//                return true;
//            }
//        }

        return true;
    }

}
