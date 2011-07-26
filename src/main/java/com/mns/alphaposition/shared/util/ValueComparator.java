package com.mns.alphaposition.shared.util;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;


public class ValueComparator implements Comparator {

    Map base;

    public ValueComparator(Map base) {
        this.base = base;
    }

    public int compare(Object a, Object b) {
        return ((BigDecimal) base.get(b)).compareTo((BigDecimal) base.get(a));
    }
}