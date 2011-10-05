package com.mns.alphaposition.server.engine.model;

import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.Id;
import java.util.List;

public class RankingList {

    @Id
    private String date;

    @Unindexed
    private List<String> symbols;

    public RankingList() {
        //no arg for objectify
    }

    public RankingList(String date, List<String> symbols) {
        this.date = date;
        this.symbols = symbols;
    }

    public List<String> getSymbols() {
        return symbols;
    }

}
