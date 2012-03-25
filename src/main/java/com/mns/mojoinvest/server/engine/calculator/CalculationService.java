package com.mns.mojoinvest.server.engine.calculator;

import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CalculationService {

    private final QuoteDao quoteDao;

    public CalculationService(QuoteDao quoteDao) {
        this.quoteDao = quoteDao;
    }

    public List<CalculatedValue> calculateSMA(Fund fund, LocalDate fromDate, LocalDate toDate, int period) {

        List<CalculatedValue> cvs = new ArrayList<CalculatedValue>();

        List<LocalDate> dates = TradingDayUtils.getWeeklySeries(fromDate, toDate, 1, true);

        SMACalculator calculator = new SMACalculator(period);
        for (Quote quote : quoteDao.get(fund, dates)) {
            calculator.newNum(quote.getAdjClose());
            BigDecimal avg = calculator.getAvg();
            System.out.println(quote.getDate() + " " + quote.getAdjClose() + " " + avg);
            if (avg != null) {
                CalculatedValue cv = new CalculatedValue(quote.getDate(), quote.getSymbol(),
                        "SMA", period, avg);
                cvs.add(cv);
            }
        }
        return cvs;
    }


}
