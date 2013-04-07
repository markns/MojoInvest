package com.mns.mojoinvest.server.engine.model;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Serialized;
import org.apache.commons.math.linear.RealMatrix;
import org.joda.time.LocalDate;

import javax.persistence.Id;
import java.text.DecimalFormat;
import java.util.List;

@Cached
public class Correlation {

    @Id
    private String id;

    private List<String> symbols;

    @Serialized
    private RealMatrix realMatrix;

    public Correlation() {
    }

    public Correlation(LocalDate date, int period, List<String> symbols, RealMatrix realMatrix) {
        this.id = date + "|" + period;
        this.symbols = symbols;
        this.realMatrix = realMatrix;
    }


    public double get(String symbol1, String symbol2) {
        return realMatrix.getEntry(symbols.indexOf(symbol1), symbols.indexOf(symbol2));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        DecimalFormat f = new DecimalFormat("0.00000");
        sb.append("\t");
        for (String symbol : symbols) {
            sb.append(symbol).append("\t");
        }
        sb.append("\n");

        for (String symbol1 : symbols) {
            sb.append(symbol1).append("\t");
            for (String symbol2 : symbols) {
                sb.append(f.format(get(symbol1, symbol2))).append("\t");
            }
            sb.append("\n");

        }
        return sb.toString();
    }
}
