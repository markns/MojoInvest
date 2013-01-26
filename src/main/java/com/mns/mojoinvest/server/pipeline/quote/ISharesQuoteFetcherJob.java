package com.mns.mojoinvest.server.pipeline.quote;


import com.google.appengine.tools.pipeline.*;
import com.google.appengine.tools.pipeline.impl.model.JobRecord;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.pipeline.PipelineException;
import com.mns.mojoinvest.server.pipeline.PipelineHelper;
import com.mns.mojoinvest.server.util.QuoteUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import jxl.Workbook;
import org.joda.time.LocalDate;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Arrays;
import java.util.List;

public class ISharesQuoteFetcherJob extends Job2<String, String, String> {

    public static final String BASE_URL = "http://tools.ishares.com/tec6/download_data.do";

    @Override
    public Value<String> run(String fundId, String sessionId) {

        ClientResponse response = downloadISharesData(fundId, sessionId);
        //response from webservice is 200 regardless of success
        //content-disposition is only set if a real file is returned
        if (response.getHeaders().containsKey("content-disposition")) {
            return processResponse(fundId, response);
        } else {
            return processFailedResponse(fundId);
        }
    }

    private ClientResponse downloadISharesData(String fundId, String sessionId) {
        Client client = PipelineHelper.getClient();

        WebResource webResource = client.resource(BASE_URL);
        WebResource.Builder builder = webResource.getRequestBuilder();

        builder.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        builder.cookie(new Cookie("JSESSIONID", sessionId));

        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.put("action", Arrays.asList("downloadByFund"));
        formData.put("fundId", Arrays.asList(fundId));

        return builder.post(ClientResponse.class, formData);
    }

    private Value<String> processResponse(String fundId, ClientResponse response) {
        Workbook workbook;
        try {
            workbook = Workbook.getWorkbook(response.getEntityInputStream());
            List<Quote> quotes = ISharesExcelParser.parse(workbook);
            QuoteDao dao = PipelineHelper.getQuoteDao();

            //Todo: Test for changes
            if (testForChanges(quotes)) {
                //Todo: set property requiring all cvs be recalculated
            }

            quotes.addAll(QuoteUtils.rollMissingQuotes(quotes));

            dao.put(quotes);
            updateQuoteDatesOnFund(quotes);
            return immediate(fundId + " quote retrieval complete");

        } catch (Exception e) {
            String filename = response.getHeaders().get("content-disposition").get(0);
            throw new PipelineException("Unable to process excel file " + filename + " for fund " + fundId, e);
        }
    }

    private static void updateQuoteDatesOnFund(List<Quote> quotes) {

        if (quotes.size() > 0) {
            LocalDate earliestQuote = quotes.get(0).getDate();
            LocalDate latestQuote = quotes.get(0).getDate();
            for (Quote quote : quotes) {
                if (quote.getDate().isBefore(earliestQuote))
                    earliestQuote = quote.getDate();
                if (quote.getDate().isAfter(latestQuote)) {
                    latestQuote = quote.getDate();
                }
            }
            FundDao dao = PipelineHelper.getFundDao();
            Fund fund = dao.get(quotes.get(0).getSymbol());
            fund.setEarliestQuoteDate(earliestQuote);
            fund.setLatestQuoteDate(latestQuote);
            dao.put(fund);
        }
    }

    private Value<String> processFailedResponse(String fundId) {
        PipelineService service = PipelineServiceFactory.newPipelineService();

        JobRecord jobRecord;
        try {
            jobRecord = (JobRecord) service.getJobInfo(getJobKey().getName());
            if (jobRecord.getAttemptNumber() == 2) {
                return immediate("Unable to retrieve quotes for fund " + fundId + " after " +
                        jobRecord.getAttemptNumber() + " attempts - aborting");
            } else {
                throw new PipelineException("Unable to retrieve quotes for fund " + fundId + " on attempt " +
                        jobRecord.getAttemptNumber() + " - retrying");
            }
        } catch (NoSuchObjectException e) {
            throw new PipelineException("Unable to find JobRecord for fund " + fundId + " during failure processing", e);
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
