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
public class Category {

    @Id
    private String id;

    private Set<String> symbols = new HashSet<String>();

    public Category() {
    }

    public Category(String category) {
        this.id = category;
    }

    public boolean add(String s) {
        return symbols.add(s);
    }

    public Set<String> getSymbols() {
        return symbols;
    }


}
