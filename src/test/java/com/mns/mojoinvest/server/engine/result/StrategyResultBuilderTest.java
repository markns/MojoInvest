package com.mns.mojoinvest.server.engine.result;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.objectify.ObjectifyFundDao;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

public class StrategyResultBuilderTest {

    @Mock
    private QuoteDao quoteDao;

    //    private RankingDao rankingDao = new RankingDao(ObjectifyService.factory());
    private FundDao fundDao = new ObjectifyFundDao(ObjectifyService.factory());

    private final LocalDatastoreServiceTestConfig config = new LocalDatastoreServiceTestConfig();
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(config);


    @Test
    public void testBuild() throws Exception {

    }

    @Test
    public void testGenerateDataTable() throws Exception, ResultBuilderException {
        StrategyResultBuilder builder = new StrategyResultBuilder(quoteDao, fundDao);
        builder.generateDataTable(new ArrayList<LocalDate>());
    }
}
