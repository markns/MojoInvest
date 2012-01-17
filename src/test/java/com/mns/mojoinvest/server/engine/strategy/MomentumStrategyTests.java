package com.mns.mojoinvest.server.engine.strategy;

import com.googlecode.objectify.ObjectifyService;
import com.mns.mojoinvest.server.engine.execution.Executor;
import com.mns.mojoinvest.server.engine.execution.NextTradingDayExecutor;
import com.mns.mojoinvest.server.engine.model.dao.FundDao;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.model.dao.RankingDao;
import org.junit.Test;

import java.util.LinkedList;

import static org.mockito.Mockito.*;

public class MomentumStrategyTests {

    private final QuoteDao quoteDao = new QuoteDao(ObjectifyService.factory());

    private final RankingDao rankingDao = new RankingDao(ObjectifyService.factory());

    private final FundDao fundDao = new FundDao(ObjectifyService.factory());

    private final Executor executor = new NextTradingDayExecutor(quoteDao);

    MomentumStrategy strategy = new MomentumStrategy(executor, rankingDao, fundDao);


    @Test
    public void testStrategy() {
        //You can mock concrete classes, not only interfaces
        LinkedList mockedList = mock(LinkedList.class);

        //stubbing
        when(mockedList.get(0)).thenReturn("first");
        when(mockedList.get(1)).thenThrow(new RuntimeException());
        //following prints "first"
        System.out.println(mockedList.get(0));

        //following throws runtime exception
        System.out.println(mockedList.get(1));
        //following prints "null" because get(999) was not stubbed
        System.out.println(mockedList.get(999));
        //Although it is possible to verify a stubbed invocation, usually it's just redundant
        //If your code cares what get(0) returns then something else breaks (often before even verify() gets executed).
        //If your code doesn't care what get(0) returns then it should not be stubbed. Not convinced? See here.
        verify(mockedList).get(0);

    }

}
