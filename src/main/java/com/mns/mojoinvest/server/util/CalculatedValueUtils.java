package com.mns.mojoinvest.server.util;

import com.google.common.base.Joiner;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;

public class CalculatedValueUtils {

    public static String[] toStringArray(CalculatedValue cv) {
        return new String[]{
                cv.getDate() + "|" + cv.getValue()};
    }

    public static String toString(CalculatedValue cv) {
        return "\"" + Joiner.on("\",\"").join(toStringArray(cv)) + "\"";
    }
}
