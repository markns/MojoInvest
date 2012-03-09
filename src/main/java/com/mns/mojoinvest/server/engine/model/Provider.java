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
public class Provider {

    @Id
    private String id;

    private Set<String> symbols = new HashSet<String>();

    public Provider() {
    }

    public Provider(String provider) {
        this.id = provider;
    }

    public boolean add(String s) {
        return symbols.add(s);
    }

    public Set<String> getSymbols() {
        return symbols;
    }
}
