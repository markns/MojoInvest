package com.mns.mojoinvest.server.engine.model;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Cached
@Entity
@Unindexed
public class CalculatedValue {

    @Id
    private final String key;

    private final BigDecimal val;

    public CalculatedValue(String key, BigDecimal val) {
        this.key = key;
        this.val = val;
    }

    public BigDecimal getVal() {
        return val;
    }
}
