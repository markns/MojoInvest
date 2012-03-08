package com.mns.mojoinvest.server.engine.model;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Serialized;
import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.Id;
import java.util.Set;

@Cached
@Unindexed
@Entity
public class Funds {

    private static final long serialVersionUID = 1L;

    @Id
    String id = "funds";   //There will only be one of these entities

    @Serialized
    Set<Fund> funds;

    public Funds() {
        //Serialization
    }

    public Funds(Set<Fund> funds) {
        this.funds = funds;
    }

    public Set<Fund> getFunds() {
        return funds;
    }
}
