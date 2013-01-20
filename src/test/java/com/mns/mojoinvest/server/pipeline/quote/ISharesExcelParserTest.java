package com.mns.mojoinvest.server.pipeline.quote;

import com.mns.mojoinvest.server.engine.model.Quote;
import junit.framework.Assert;
import jxl.Workbook;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class ISharesExcelParserTest {


    @Test
    public void testParse() throws Exception {

        InputStream is = this.getClass().getClassLoader().getResourceAsStream("DUB_alternatives.xls");
        Workbook workbook = Workbook.getWorkbook(is);

        List<Quote> quotes = ISharesExcelParser.parse(workbook);
        Assert.assertEquals(22795, quotes.size());

    }


}
