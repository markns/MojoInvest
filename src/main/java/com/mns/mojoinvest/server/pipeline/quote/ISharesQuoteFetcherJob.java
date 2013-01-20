package com.mns.mojoinvest.server.pipeline.quote;


import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.mns.mojoinvest.server.pipeline.PipelineException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import jxl.Cell;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

public class ISharesQuoteFetcherJob extends Job1<String, String> {


    public static final String BASE_URL = "http://tools.ishares.com/tec6/download_data.do";

    private static DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    String[] categories = new String[]{"DUB_alternatives", "DUB_developedequity", "DUB_emergingequity", "DUB_fixedincome"};

    private BigDecimal getBigDecimal(Cell cell) {
        if (cell.getType() == CellType.NUMBER) {
            return new BigDecimal(((NumberCell) cell).getValue());
        }
        return null;
    }

    @Override
    public Value<String> run(String sessionId) {

        for (String category : categories) {

            Client client = Client.create();
//            client.addFilter(new LoggingFilter(System.out));

            client.setFollowRedirects(true);
            client.setReadTimeout(10000);
            client.setConnectTimeout(10000);

            WebResource webResource = client.resource(BASE_URL);
            WebResource.Builder builder = webResource.getRequestBuilder();

//            builder.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//            builder.header("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
//            builder.header("Accept-Language", "en-US,en;q=0.8");
//            builder.header("Connection", "keep-alive");
//            builder.header("Host", "uk.ishares.com");
//            builder.header("Referer", "http://uk.ishares.com/en/rc/tools/performance-chart");
//            builder.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.101 Safari/537.11");

            builder.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
//            builder.cookie(new Cookie("JSESSIONID", "2A07387FEF8389899008894458DC4953.isharestools-pea02"));
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
            } catch (IOException e) {
                throw new PipelineException("Unable to retrieve excel file " + category, e);
            } catch (BiffException e) {
                throw new PipelineException("Unable to retrieve excel file " + category, e);
            }

            for (Cell cell : workbook.getSheet(0).getRow(0)) {
                System.out.println(cell.getContents());
            }
        }

        return immediate("quote retrieval done");
    }


}
