package com.mns.mojoinvest.server.engine.model;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.Id;
import java.util.HashSet;
import java.util.Set;

@Cached
@Unindexed
@Entity
public class Symbols {

    private static final long serialVersionUID = 1L;

    @Id
    private String id = "symbols";   //There will only be one of these entities

    private Set<String> symbols = new HashSet<String>();

    public Symbols() {
        //Serialization
    }

    public Symbols(Set<String> symbols) {
        this.symbols = symbols;
    }

    public Set<String> getSymbols() {
        return symbols;
    }
}
