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
import java.util.List;

public class ISharesQuoteFetcherJob extends Job2<String, String, String> {

    public static final String BASE_URL = "http://tools.ishares.com/tec6/download_data.do";

    @Override
    public Value<String> run(String fundId, String sessionId) {

        ClientResponse response = downloadISharesData(fundId, sessionId);

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
            updateQuoteDatesOnFund(quotes);

        } catch (IOException e) {
            throw new PipelineException("Unable to retrieve excel file " + fundId, e);
        } catch (BiffException e) {
            throw new PipelineException("Unable to retrieve excel file " + fundId, e);
        }

        return immediate(fundId + " quote retrieval complete");
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
