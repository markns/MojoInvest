package com.mns.alphaposition.server.engine.model;


import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.Id;
import java.util.List;

@Cached
public class Ranking {

    private static final Splitter SPLITTER = Splitter.on(',')
            .trimResults()
            .omitEmptyStrings();

    @Id
    private String date;

    @Unindexed
    private String m9;

    public Ranking() {
        //no arg for objectify
    }

    public Ranking(String date, String m9) {
        this.date = date;
        this.m9 = m9;
    }

    public List<String> getM9() {
        return toList(SPLITTER.split(m9));
    }

    private static <E> List<E> toList(Iterable<E> iterable) {
        return (iterable instanceof List)
                ? (List<E>) iterable
                : Lists.newArrayList(iterable.iterator());
    }

}
