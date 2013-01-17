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
            client.setFollowRedirects(true);

            WebResource perfChart = client.resource("http://uk.ishares.com/en/rc/tools/performance-chart");
            ClientResponse r = perfChart.get(ClientResponse.class);
            List<NewCookie> cookies = r.getCookies();

//            System.out.println(r);

            WebResource tec_passthrough = client.resource("http://uk.ishares.com/tools/tec_passthrough.jsp");
            WebResource.Builder builder2 = tec_passthrough.getRequestBuilder();
            for (NewCookie cookie : cookies) {
                builder2.cookie(cookie);
            }

            ClientResponse r2 = builder2.get(ClientResponse.class);
            List<NewCookie> cookies2 = r2.getCookies();


            WebResource webResource = client.resource(BASE_URL);
            WebResource.Builder builder = webResource.getRequestBuilder();


            builder.cookie(new Cookie("JSESSIONID_EU", "B9FCF4507B9DA08AA4E7A8E8C3F45C90.isharesnet-pea01", "/", ""));

//            Request URL:http://uk.ishares.com/en/rc/tools/performance-chart
//            Request Method:GET
//            Status Code:200 OK
//            Request Headersview source
//            Accept:text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
//Accept-Charset:ISO-8859-1,utf-8;q=0.7,*;q=0.3
//Accept-Encoding:gzip,deflate,sdch
//Accept-Language:en-US,en;q=0.8
//Connection:keep-alive
//Host:uk.ishares.com
//User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.101 Safari/537.11
//Response Headersview source
//Cache-Control:no-cache
//Connection:keep-alive
//Content-Encoding:gzip
//Content-Length:7143
//Content-Type:text/html;charset=UTF-8
//Date:Wed, 16 Jan 2013 19:38:57 GMT
//Expires:Thu, 01 Jan 1970 00:00:00 GMT
//Pragma:no-cache
//Server:Apache
//Set-Cookie:JSESSIONID_EU=C7ECC64D01AA1064954681315E46C968.isharesnet-pea02; Path=/; HttpOnly
//Set-Cookie:JSESSIONID_EU=C7ECC64D01AA1064954681315E46C968.isharesnet-pea02; Domain=.ishares.com; Path=/
//Set-Cookie:JSESSIONID_EU=C7ECC64D01AA1064954681315E46C968.isharesnet-pea02; Domain=.ishares.com; Path=/
//Set-Cookie:JSESSIONID_EU=C7ECC64D01AA1064954681315E46C968.isharesnet-pea02; Domain=.ishares.com; Path=/
//Set-Cookie:visitorIdNew=474140418; Domain=.ishares.com; Expires=Thu, 16-Jan-2014 19:38:57 GMT; Path=/
//Vary:Accept-Encoding


//            http://uk.ishares.com/tools/tec_passthrough.jsp?symbol=
//            Request Method:GET
//            Status Code:302 Moved Temporarily
//            Request Headersview source
//            Accept:text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
//Accept-Charset:ISO-8859-1,utf-8;q=0.7,*;q=0.3
//Accept-Encoding:gzip,deflate,sdch
//Accept-Language:en-US,en;q=0.8
//Connection:keep-alive
//Cookie:JSESSIONID_EU=C7ECC64D01AA1064954681315E46C968.isharesnet-pea02; JSESSIONID_EU=C7ECC64D01AA1064954681315E46C968.isharesnet-pea02; visitorIdNew=474140418
//Host:uk.ishares.com
//Referer:http://uk.ishares.com/en/rc/tools/performance-chart
//User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.101 Safari/537.11
//Query String Parametersview URL encoded
//symbol:
//Response Headersview source
//Connection:keep-alive
//Content-Length:0
//Content-Type:text/html;charset=UTF-8
//Date:Wed, 16 Jan 2013 19:38:59 GMT
//Location:http://tools.ishares.com/tec6/sso_receive.jsp?pld=X42jwPXlVPIH2AwWJp72GkKlgtBxPu0sXWkZVZdcWxnZAxCq62vSt4jCXTAI3KgDEcnohQsjqG0uKCrt+hgwcxrtm5/1CcnmNmKYmt1qUZFhzdtt7bHiYQ==&rdr=/tec6/view_chart.do&err=eu.ishares.com/tools/tec_passthrough.jsp
//Server:Apache
//Vary:Accept-Encoding

//            Request URL:http://tools.ishares.com/tec6/sso_receive.jsp?pld=X42jwPXlVPIH2AwWJp72GkKlgtBxPu0sXWkZVZdcWxnZAxCq62vSt4jCXTAI3KgDEcnohQsjqG0uKCrt+hgwcxrtm5/1CcnmNmKYmt1qUZFhzdtt7bHiYQ==&rdr=/tec6/view_chart.do&err=eu.ishares.com/tools/tec_passthrough.jsp
//            Request Method:GET
//            Status Code:302 Moved Temporarily
//            Request Headersview source
//            Accept:text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
//Accept-Charset:ISO-8859-1,utf-8;q=0.7,*;q=0.3
//Accept-Encoding:gzip,deflate,sdch
//Accept-Language:en-US,en;q=0.8
//Connection:keep-alive
//Cookie:JSESSIONID_EU=C7ECC64D01AA1064954681315E46C968.isharesnet-pea02; visitorIdNew=474140418; UnicaNIODID=DXIUKnxjTu7-X4sZqyo
//Host:tools.ishares.com
//Referer:http://uk.ishares.com/en/rc/tools/performance-chart
//User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.101 Safari/537.11
//Query String Parametersview URL encoded
//pld:X42jwPXlVPIH2AwWJp72GkKlgtBxPu0sXWkZVZdcWxnZAxCq62vSt4jCXTAI3KgDEcnohQsjqG0uKCrt hgwcxrtm5/1CcnmNmKYmt1qUZFhzdtt7bHiYQ
//rdr:/tec6/view_chart.do
//err:eu.ishares.com/tools/tec_passthrough.jsp
//Response Headersview source
//Cache-Control:max-age=3600
//Connection:keep-alive
//Content-Length:0
//Content-Type:text/plain; charset=UTF-8
//Date:Wed, 16 Jan 2013 19:39:00 GMT
//Expires:Wed, 16 Jan 2013 20:39:00 GMT
//Location:http://tools.ishares.com/tec6/view_chart.do
//Server:Apache
//Set-Cookie:JSESSIONID=7BB91AE1DA59C529B5174D09D63CC248.isharestools-pea01; Path=/tec6; HttpOnly
//X-HP-CAM-COLOR:V=1;ServerAddr=Zs8gt0JSCyeIUPybiP/vpw==;GUID=1|oHIiua0EQHEvHesMm0g3WnA5qkrQ_NU3dFQoDKKF_EBdfaIHv_yFPHIfdKU__Are7Fl0XgBS_B-QUqbUn4_KcA..|L3RlYzYvc3NvX3JlY2VpdmUuanNw


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
