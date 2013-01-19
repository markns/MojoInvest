package com.mns.mojoinvest.server.pipeline.quote;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
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
import javax.ws.rs.core.NewCookie;
import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

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
            client.addFilter(new LoggingFilter(System.out));

            client.setFollowRedirects(true);

            WebResource perfChart = client.resource("http://uk.ishares.com/en/rc/tools/performance-chart");
            ClientResponse r = perfChart.get(ClientResponse.class);
            List<NewCookie> cookies = r.getCookies();

//            System.out.println(r);

            WebResource tec_passthrough = client.resource("http://uk.ishares.com/tools/tec_passthrough.jsp");
            WebResource.Builder builder2 = tec_passthrough.queryParam("symbol=", "").getRequestBuilder();


            builder2.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            builder2.header("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
//            builder2.header("Accept-Encoding", "gzip,deflate,sdch");
            builder2.header("Accept-Language", "en-US,en;q=0.8");
            builder2.header("Connection", "keep-alive");
            builder2.header("Host", "uk.ishares.com");
            builder2.header("Referer", "http://uk.ishares.com/en/rc/tools/performance-chart");
            builder2.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.101 Safari/537.11");
            for (NewCookie cookie : cookies) {
                builder2.cookie(cookie);
            }

            ClientResponse r2 = builder2.get(ClientResponse.class);
            List<NewCookie> cookies2 = r2.getCookies();


            WebResource sso_receive = client.resource("http://tools.ishares.com/tec6/sso_receive.jsp");
            WebResource.Builder builder3 = sso_receive
//            pld:1HkPp ZzIgsj839eaj9H3EKlgtBxPu0sXWkZVZdcWxnZAxCq62vSt4jCXTAI3KgDEcnohQsjqG0uKCrt hgwcxrtm5/1CcnmNmKYmt1qUZFhzdtt7bHiYQ
                    .queryParam("rdr", "/tec6/view_chart.do")
                    .queryParam("err", "eu.ishares.com/tools/tec_passthrough.jsp")
                    .getRequestBuilder();


            builder3.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            builder3.header("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
//            builder3.header("Accept-Encoding", "gzip,deflate,sdch");
            builder3.header("Accept-Language", "en-US,en;q=0.8");
            builder3.header("Connection", "keep-alive");
            builder3.header("Host", "uk.ishares.com");
            builder3.header("Referer", "http://uk.ishares.com/en/rc/tools/performance-chart");
            builder3.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.101 Safari/537.11");
            for (NewCookie cookie : cookies) {
                builder3.cookie(cookie);
            }

            ClientResponse r3 = builder3.get(ClientResponse.class);
            List<NewCookie> cookies3 = r3.getCookies();


            WebResource webResource = client.resource(BASE_URL);
            WebResource.Builder builder = webResource.getRequestBuilder();

            MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
            formData.put("action", Arrays.asList("downloadByCategory"));
            formData.put("fromDate", Arrays.asList(""));
            formData.put("toDate", Arrays.asList(""));
            formData.put("categoryCode", Arrays.asList(category));

//            JSESSIONID=;Version=1;Path=/tec6
            ClientResponse response = builder.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                    .cookie(new Cookie("JSESSIONID", "6A58120E970B81A7709A4344FAA0F508.isharestools-pea01"))
                    .post(ClientResponse.class, formData);

            Workbook workbook = Workbook.getWorkbook(response.getEntityInputStream());

            for (Cell cell : workbook.getSheet(0).getRow(0)) {
                System.out.println(cell.getContents());
            }
        }

    }


}
