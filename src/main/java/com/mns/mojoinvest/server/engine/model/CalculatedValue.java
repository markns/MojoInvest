package com.mns.mojoinvest.server.engine.model;

import com.google.common.base.Splitter;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;
import org.joda.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.logging.Logger;

@Cached
@Entity
@Unindexed
public class CalculatedValue {

    private static final Logger log = Logger.getLogger(CalculatedValue.class.getName());

    @Id
    private String key;

    private BigDecimal value;

    private String dateStr;
    private String symbol;
    private String type;
    private int period;

    public CalculatedValue() {
    }

    public CalculatedValue(String key, BigDecimal value) {
        this.key = key;
        splitKey();
        this.value = value;
    }

    public CalculatedValue(LocalDate date, String symbol, String type, int period, BigDecimal value) {
        this.key = getCalculatedValueKey(date, symbol, type, period);
        this.dateStr = date.toString();
        this.symbol = symbol;
        this.type = type;
        this.period = period;
        this.value = value;
    }

    public CalculatedValue(LocalDate date, String symbol, String type, int period, double value) {
        this.key = getCalculatedValueKey(date, symbol, type, period);
        this.dateStr = date.toString();
        this.symbol = symbol;
        this.type = type;
        this.period = period;
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            this.value = BigDecimal.ZERO;
        } else {
            this.value = new BigDecimal(value, MathContext.DECIMAL32).setScale(3, RoundingMode.HALF_EVEN);
        }
    }

    public static String getCalculatedValueKey(LocalDate date, String symbol, String type, int period) {
        return date + "|" + symbol + "|" + type + "|" + period;
    }

    public String getDateStr() {
        if (dateStr == null)
            splitKey();
        return dateStr;
    }

    public String getSymbol() {
        if (symbol == null)
            splitKey();
        return symbol;
    }

    public String getType() {
        if (type == null)
            splitKey();
        return type;
    }

    public int getPeriod() {
        if (period == 0)
            splitKey();
        return period;
    }

    private void splitKey() {
//        long x = System.nanoTime();
        Iterator<String> s = SPLITTER.split(key).iterator();
//        log.fine("String split took " + (System.nanoTime() - x));

//        x = System.nanoTime();
        dateStr = s.next();
//        log.fine("New LocalDate took " + (System.nanoTime() - x));

//        x = System.nanoTime();
        symbol = s.next();
//        log.fine("symbol took " + (System.nanoTime() - x));

//        x = System.nanoTime();
        type = s.next();
//        log.fine("type took " + (System.nanoTime() - x));

//        x = System.nanoTime();
        period = Integer.parseInt(s.next());
//        log.fine("Integer parseInt took " + (System.nanoTime() - x));
    }


    private static final Splitter SPLITTER = Splitter.on('|')
            .trimResults()
            .omitEmptyStrings();

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CalculatedValue{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }

    public static CalculatedValue fromStrArr(String[] row) {
        return new CalculatedValue(row[0], new BigDecimal(row[1]));
    }

    public String[] toStrArr() {
        return new String[]{
                key,
                value + ""};
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalculatedValue that = (CalculatedValue) o;

        if (!key.equals(that.key)) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
