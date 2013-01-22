package com.mns.mojoinvest.server.pipeline;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

public class TestDownload {

    public static final String BASE_URL = "http://tools.ishares.com/tec6/download_data.do";

    String[] s = new String[]{"157746",
            "200307",
            "200308"
//            "216254",
//            "157922",
//            "200178",
//            "200280",
//            "200180",
//            "157909",
//            "218916",
//            "200273",
//            "157496",
//            "216249",
//            "200150",
//            "200179",
//            "200307",
//            "157850",
//            "157501",
//            "200281",
//            "157745",
//            "200177",
//            "157839",
//            "216253",
//            "157921",
//            "200311",
//            "157911",
//            "200222",
//            "200272",
//            "157739",
//            "157858",
//            "157495",
//            "157882",
//            "219256",
//            "200306",
//            "157844",
//            "157502",
//            "200176",
//            "157744",
//            "215133",
//            "200149",
//            "200282",
//            "200139",
//            "200221",
//            "157910",
//            "200312",
//            "219246",
//            "157499",
//            "157836",
//            "214967",
//            "200129",
//            "216252",
//            "157498",
//            "219257",
//            "200309",
//            "157793",
//            "200182",
//            "157503",
//            "219267",
//            "200148",
//            "157743",
//            "200200",
//            "200175",
//            "157904",
//            "200283",
//            "214999",
//            "200220",
//            "216251",
//            "200313",
//            "214968",
//            "157816",
//            "200308",
//            "157497",
//            "200181",
//            "157792",
//            "157846",
//            "214930",
//            "218720",
//            "157870",
//            "216250",
//            "157841",
//            "157834",
//            "200314",
//            "214965",
//            "200184",
//            "216235",
//            "157796",
//            "215135",
//            "219240",
//            "200328",
//            "218676",
//            "200147",
//            "157869",
//            "157742",
//            "219286",
//            "214931",
//            "157795",
//            "216255",
//            "157814",
//            "157849",
//            "200132",
//            "200080",
//            "157835",
//            "157840",
//            "214966",
//            "200183",
//            "157741",
//            "157740",
//            "214932",
//            "216256",
//            "157749",
//            "157794",
//            "200153",
//            "200081",
//            "219266",
//            "200131",
//            "157531",
//            "157843",
//            "157920",
//            "200279",
//            "200199",
//            "157913",
//            "157750",
//            "200152",
//            "215023",
//            "157883",
//            "200134",
//            "157532",
//            "157494",
//            "157748",
//            "157738",
//            "157530",
//            "157842",
//            "214929",
//            "157871",
//            "215136",
//            "157751",
//            "157500",
//            "200151",
//            "200133",
//            "219264",
//            "157533",
//            "157747"};
    };

    @Test
    public void testDL() throws Exception {
        Client client = PipelineHelper.getClient();

        for (String fundId : s) {
            WebResource webResource = client.resource(BASE_URL);
            WebResource.Builder builder = webResource.getRequestBuilder();

            builder.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
            builder.cookie(new Cookie("JSESSIONID", "3902AE034D1F9D9C9D2531C57E0E0076.isharestools-pea02"));

            MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
            formData.put("action", Arrays.asList("downloadByFund"));
//        formData.put("tkr_unica", Arrays.asList("DJSC"));
//        formData.put("fundSearchText", Arrays.asList("iShares EURO STOXX Small"));
//        formData.put("currencyCode", Arrays.asList("EUR"));
            formData.put("fundId", Arrays.asList(fundId));

            ClientResponse response = builder.post(ClientResponse.class, formData);


            FileOutputStream fos = new FileOutputStream("data/tmp/" + fundId + ".xls");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IOUtils.copy(response.getEntityInputStream(), bos);
            fos.write(bos.toByteArray());
            bos.close();
            fos.close();

        }

    }
}
