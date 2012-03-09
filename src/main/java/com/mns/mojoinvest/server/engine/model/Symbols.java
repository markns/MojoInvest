package com.mns.mojoinvest.server.engine.model;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.Id;
import java.util.Set;

@Cached
@Unindexed
@Entity
public class Symbols {

    private static final long serialVersionUID = 1L;

    @Id
    String id = "symbols";   //There will only be one of these entities

    Set<String> symbols;

    public Symbols() {
        //Serialization
    }

    public Symbols(Set<String> symbols) {
        this.symbols = symbols;
    }

    public Set<String> getsymbols() {
        return symbols;
    }
}
