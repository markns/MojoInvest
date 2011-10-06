package com.mns.alphaposition.server.engine.model;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

@Cached
public class RankingText {

    private static final Splitter SPLITTER = Splitter.on(',')
            .trimResults()
            .omitEmptyStrings();

    @Id
    private String key;

    @Unindexed
    private String symbols;

    @Transient
    private List<String> symbolsList;

    @Unindexed
    private String values;

    public RankingText() {
        //no arg for objectify
    }

    public RankingText(String key, String symbols, String values) {
        this.key = key;
        this.symbols = symbols;
        this.values = values;
    }

    public String getKey() {
        return key;
    }

    public List<String> getSymbols() {
        //TODO: Should we just cache these values as a map?
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
        return "RankingText{" +
                "key='" + key + '\'' +
                ", symbols='" + symbols + '\'' +
                '}';
    }


}
