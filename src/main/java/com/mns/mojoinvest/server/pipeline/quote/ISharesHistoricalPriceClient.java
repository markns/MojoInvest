package com.mns.mojoinvest.server.pipeline.quote;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import jxl.Cell;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Workbook;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;

public class ISharesHistoricalPriceClient {


    public static final String BASE_URL = "http://tools.ishares.com/tec6/download_data.do";

    public static final String OUTPUT_DIR = "/Users/marknuttallsmith/Projects/ETFData/data";

    private static DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static final String SLASH = File.separator;

//    WebServiceClient wsClient = new WebServiceClient();


    public static void main(String[] args) throws Exception {
        ISharesHistoricalPriceClient client = new ISharesHistoricalPriceClient();
        client.run();
//        client.parseDividends();
    }

    String[] categories = new String[]{"DUB_alternatives"};//;, "DUB_developedequity", "DUB_emergingequity", "DUB_fixedincome"};

    private BigDecimal getBigDecimal(Cell cell) {
        if (cell.getType() == CellType.NUMBER) {
            return new BigDecimal(((NumberCell) cell).getValue());
        }
        return null;
    }

    private void run() throws Exception {

        for (String category : categories) {

            Client client = Client.create();
            client.setFollowRedirects(true);

            WebResource webResource = client.resource(BASE_URL);
            WebResource.Builder builder = webResource.getRequestBuilder();


            builder.cookie(new Cookie("JSESSIONID", "A6F242D5E2CEC4AF5083B020420129EE.isharestools-pea01"));

            MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
            formData.put("action", Arrays.asList("downloadByCategory"));
            formData.put("fromDate", Arrays.asList(""));
            formData.put("toDate", Arrays.asList(""));
            formData.put("categoryCode", Arrays.asList(category));

            ClientResponse response = builder.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                    .post(ClientResponse.class, formData);

            Workbook workbook = Workbook.getWorkbook(response.getEntityInputStream());

            for (Cell cell : workbook.getSheet(0).getRow(0)) {
                System.out.println(cell.getContents());
            }
        }

    }


}
