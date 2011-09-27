package com.mns.alphaposition.server.engine.model;

import com.googlecode.objectify.annotation.Cached;
import org.joda.time.LocalDate;

@Cached
public class Ranking {

    private LocalDate date;

    private String m9;

    public Ranking(LocalDate date, String m9) {
        this.date = date;
        this.m9 = m9;
    }
}
