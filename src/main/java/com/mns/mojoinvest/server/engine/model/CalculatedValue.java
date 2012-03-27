package com.mns.mojoinvest.server.engine.model;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;
import org.joda.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

@Cached
@Entity
@Unindexed
public class CalculatedValue {

    @Id
    private String key;

    private BigDecimal value;

    private transient LocalDate date;
    private transient String symbol;
    private transient String type;
    private transient int period;

    public CalculatedValue() {
    }

    public CalculatedValue(String key, BigDecimal value) {
        this.key = key;
        this.value = value;
    }

    public CalculatedValue(LocalDate date, String symbol, String type, int period, BigDecimal value) {
        this.key = date + "|" + symbol + "|" + type + "|" + period;
        this.value = value;
    }

    public CalculatedValue(LocalDate date, String symbol, String type, int period, double value) {
        this.key = date + "|" + symbol + "|" + type + "|" + period;
        this.value = new BigDecimal(value, MathContext.DECIMAL32);
    }

    public LocalDate getDate() {
        if (date == null)
            splitKey();
        return date;
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
        List<String> s = toList(SPLITTER.split(key));
        date = new LocalDate(s.get(0));
        symbol = s.get(1);
        type = s.get(2);
        period = Integer.parseInt(s.get(3));

    }


    private static final Splitter SPLITTER = Splitter.on('|')
            .trimResults()
            .omitEmptyStrings();

    private static <E> List<E> toList(Iterable<E> iterable) {
        return (iterable instanceof List)
                ? (List<E>) iterable
                : Lists.newArrayList(iterable.iterator());
    }

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
}
