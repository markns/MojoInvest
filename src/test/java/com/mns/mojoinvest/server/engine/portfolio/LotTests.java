package com.mns.mojoinvest.server.engine.portfolio;

import com.mns.mojoinvest.server.engine.model.Fund;
import com.mns.mojoinvest.server.engine.transaction.BuyTransaction;
import com.mns.mojoinvest.server.engine.transaction.SellTransaction;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class LotTests {

    public static final BigDecimal ONE_HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);
    public static final BigDecimal TWO_HUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN).multiply(new BigDecimal("2"));
    public static final BigDecimal FIFTY = BigDecimal.TEN.multiply(new BigDecimal("5"));
    public static final BigDecimal TWO = BigDecimal.ONE.add(BigDecimal.ONE);

    Fund fund = new Fund("TEST", "Test fund", "Category", "Provider", true,
            "US", "Index", "Blah blah", new LocalDate("2011-01-01"));
    BuyTransaction buy = new BuyTransaction(fund, new LocalDate("2011-02-01"), ONE_HUNDRED, TWO, BigDecimal.TEN);
    SellTransaction sell1 = new SellTransaction(fund, new LocalDate("2011-03-01"), FIFTY, TWO, BigDecimal.TEN);
    SellTransaction sell2 = new SellTransaction(fund, new LocalDate("2011-04-01"), FIFTY, TWO, BigDecimal.TEN);
    SellTransaction sellTooLarge = new SellTransaction(fund, new LocalDate("2011-03-01"), TWO_HUNDRED, TWO, BigDecimal.TEN);


//Lot
//addClosingTransaction
//getInitialInvestment
//getInitialQuantity
//closed
//getRemainingQuantity
//getRemainingQuantity
//costBasis
//cashOut
//cashIn
//marketValue
//marketValue
//returnsGain
//overallReturn
//gain
//todaysGain
//gainPercentage
//getOpeningTransaction
//getClosingTransactions

    @Test
    public void testCreateNewLot() {
        Lot lot = new Lot(buy);
        assertNotNull(lot.getOpeningTransaction());
        assertEquals(lot.getClosingTransactions().size(), 0);
    }

    @Test
    public void testInitialValues() {
        Lot lot = new Lot(buy);
        //TODO: Should initial investment include transaction costs?
        assertEquals(new BigDecimal("210"), lot.getInitialInvestment());
        assertEquals(new BigDecimal("100"), lot.getInitialQuantity());
    }

    @Test
    public void testAddClosingTransaction() {

    }


    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAddTooLargeClosingTransactionFails() throws PortfolioException {
        thrown.expect(PortfolioException.class);
        Lot lot = new Lot(buy);
        lot.addClosingTransaction(sellTooLarge);
    }

    @Test
    public void testLotOpen() {

    }

    @Test
    public void testLotClosed() {

    }


}
