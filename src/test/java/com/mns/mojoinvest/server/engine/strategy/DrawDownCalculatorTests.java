package com.mns.mojoinvest.server.engine.strategy;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrawDownCalculatorTests {

    private void drawDownCalculator(List<PortfolioHistoryDetail> details) {

        List<DrawDown> drawDowns = new ArrayList<DrawDown>();
        DrawDown currentDD = new DrawDown(details.get(0).getDate(), details.get(0).getValue());

        for (PortfolioHistoryDetail detail : details) {
            LocalDate date = detail.getDate();
            BigDecimal value = detail.getValue();

            //Curve is going up, and min has not been set
            if (value.compareTo(currentDD.getMax()) > 0 &&
                    currentDD.getMin() == null) {
                currentDD.setMaxDate(date);
                currentDD.setMax(value);
            }
            //Curve is going down
            else if (value.compareTo(currentDD.getMax()) < 0) {
                //Min has not been set
                if (currentDD.getMin() == null) {
                    currentDD.setMinDate(date);
                    currentDD.setMin(value);
                }
                //New value is lower than min stored currently
                else if (value.compareTo(currentDD.getMin()) < 0) {
                    currentDD.setMinDate(date);
                    currentDD.setMin(value);
                }
            }
            //New value is higher than current max - create new drawdown
            else if (value.compareTo(currentDD.getMax()) > 0) {
                drawDowns.add(currentDD);
                currentDD = new DrawDown(date, value);
            }
            System.out.println(detail);
            System.out.println(currentDD);
        }
        drawDowns.add(currentDD);
        System.out.println(drawDowns);
    }

    private class PortfolioHistoryDetail {

        private LocalDate date;
        private BigDecimal value;

        private PortfolioHistoryDetail(LocalDate date, BigDecimal value) {
            this.date = date;
            this.value = value;
        }

        public LocalDate getDate() {
            return date;
        }

        public BigDecimal getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Detail{" +
                    "date=" + date +
                    ", value=" + value +
                    '}';
        }
    }

    @Test
    public void testDrawDowns() {

        List<PortfolioHistoryDetail> details = Arrays.asList(
                new PortfolioHistoryDetail(new LocalDate("2012-01-01"), new BigDecimal(10)),
                new PortfolioHistoryDetail(new LocalDate("2012-01-02"), new BigDecimal(11)),
                new PortfolioHistoryDetail(new LocalDate("2012-01-03"), new BigDecimal(12)),
                new PortfolioHistoryDetail(new LocalDate("2012-01-04"), new BigDecimal(13)),
                new PortfolioHistoryDetail(new LocalDate("2012-01-05"), new BigDecimal(10)),
                new PortfolioHistoryDetail(new LocalDate("2012-01-06"), new BigDecimal(9)),
                new PortfolioHistoryDetail(new LocalDate("2012-01-07"), new BigDecimal(9)),
                new PortfolioHistoryDetail(new LocalDate("2012-01-08"), new BigDecimal(13)),
                new PortfolioHistoryDetail(new LocalDate("2012-01-09"), new BigDecimal(14)),
                new PortfolioHistoryDetail(new LocalDate("2012-01-10"), new BigDecimal(16)),
                new PortfolioHistoryDetail(new LocalDate("2012-01-11"), new BigDecimal(13)),
                new PortfolioHistoryDetail(new LocalDate("2012-01-12"), new BigDecimal(13)),
                new PortfolioHistoryDetail(new LocalDate("2012-01-12"), new BigDecimal(11)),
                new PortfolioHistoryDetail(new LocalDate("2012-01-12"), new BigDecimal(14))
        );

        drawDownCalculator(details);


    }
}
