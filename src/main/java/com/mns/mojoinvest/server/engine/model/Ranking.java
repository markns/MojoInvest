package com.mns.mojoinvest.server.engine.model;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;
import org.joda.time.LocalDate;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

import static com.mns.mojoinvest.server.util.DatastoreUtils.forDatastore;

@Cached
public class Ranking {

    @Id
    private String id;

    @Unindexed
    private String symbols;

    @Unindexed
    private String values;

    @Transient
    private List<String> symbolsList;

    public Ranking() {
        //no arg for objectify
    }

    public Ranking(LocalDate date, RankingParams params, String symbols, String values) {
        this.id = createId(date, params);
        this.symbols = symbols;
        this.values = values;
    }

    public static String createId(LocalDate date, RankingParams params) {
        return forDatastore(date) + " " + params;
    }

    public String getId() {
        return id;
    }

    private static final Splitter SPLITTER = Splitter.on(',')
            .trimResults()
            .omitEmptyStrings();

    public List<String> getSymbols() {
        if (symbolsList == null) {
            symbolsList = toList(SPLITTER.split(symbols));
        }
        return symbolsList;
    }

    public List<String> getValues() {
        return toList(SPLITTER.split(values));
    }

    private static <E> List<E> toList(Iterable<E> iterable) {
        return (iterable instanceof List)
                ? (List<E>) iterable
                : Lists.newArrayList(iterable.iterator());
    }

    @Override
    public String toString() {
        return "Ranking{" +
                "id='" + id + '\'' +
                ", symbols='" + symbols + '\'' +
                '}';
    }

}
