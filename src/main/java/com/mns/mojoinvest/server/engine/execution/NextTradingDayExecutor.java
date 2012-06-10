package com.mns.mojoinvest.server.engine.execution;

import com.google.inject.Inject;
import com.mns.mojoinvest.server.engine.model.Quote;
import com.mns.mojoinvest.server.engine.model.dao.QuoteDao;
import com.mns.mojoinvest.server.engine.portfolio.Portfolio;
import com.mns.mojoinvest.server.engine.portfolio.PortfolioException;
import com.mns.mojoinvest.server.engine.portfolio.Position;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import com.mns.mojoinvest.server.util.TradingDayUtils;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.logging.Logger;

public class NextTradingDayExecutor implements Executor {

    private static final Logger log = Logger.getLogger(NextTradingDayExecutor.class.getName());

    private QuoteDao quoteDao;

    @Inject
    public NextTradingDayExecutor(QuoteDao quoteDao) {
        this.quoteDao = quoteDao;
    }

    @Override
    public void buy(Portfolio portfolio, String fund, LocalDate date, BigDecimal allocation)
            throws PortfolioException {
        //TODO: getRanking execution price should be mid between open and close of next days quote

        LocalDate executionDate = TradingDayUtils.rollForward(date.plusDays(1));
        Quote executionQuote = quoteDao.get(fund, executionDate);
        log.fine("Loaded buy execution quote " + executionQuote);

        if (executionQuote == null) {
            throw new PortfolioException(date + " Unable to buy " + fund + " - quote was null");
        }
        BigDecimal shares = allocation.divide(executionQuote.getAdjClose(), 0, BigDecimal.ROUND_DOWN);
        if (!portfolio.isShadow())
            log.fine(executionDate.dayOfWeek().getAsShortText() + " " + executionDate +
                    " Buy " + shares + " " + fund + " at " + executionQuote.getAdjClose());
        BuyTransaction tx = new BuyTransaction(fund, executionDate, shares,
                executionQuote.getAdjClose(), portfolio.getTransactionCost());
        portfolio.add(tx);
    }

    @Override
    public void sellAll(Portfolio portfolio, String fund, LocalDate date)
            throws PortfolioException {
        //TODO: getRanking execution price should be mid between open and close of next days quote
        LocalDate executionDate = TradingDayUtils.rollForward(date.plusDays(1));
        Quote executionQuote = quoteDao.get(fund, executionDate);

        log.fine("Loaded sell execution quote " + executionQuote);

        if (executionQuote == null) {
            throw new PortfolioException(date + " Unable to sell " + fund + " - quote was null");
        }
        Position position = portfolio.getPosition(fund);
        if (!portfolio.isShadow())
            log.fine(executionDate.dayOfWeek().getAsShortText() + " " + executionDate +
                    " Sell " + position.shares(executionDate) + " " + fund + " at " + executionQuote.getAdjClose());
        SellTransaction tx = new SellTransaction(fund, executionDate, position.shares(executionDate),
                executionQuote.getAdjClose(), portfolio.getTransactionCost());
        portfolio.add(tx);
    }
}
