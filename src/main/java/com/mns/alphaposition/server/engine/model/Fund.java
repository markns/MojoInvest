package com.mns.alphaposition.server.engine.model;

import com.googlecode.objectify.annotation.Cached;
import org.joda.time.LocalDate;

import javax.persistence.Id;
import java.math.BigDecimal;

@Cached
public class Fund implements Comparable<Fund> {

    @Id
    private String symbol;

    private String name;

    private String category;

    private String provider;

    private BigDecimal aum;

    private BigDecimal expenseRatio;

    private LocalDate inceptionDate;

    private BigDecimal averageVol;

    public Fund(String symbol, String name, String category, String provider, BigDecimal aum,
                BigDecimal expenseRatio, LocalDate inceptionDate, BigDecimal averageVol) {
        this.symbol = symbol;
        this.name = name;
        this.category = category;
        this.provider = provider;
        this.aum = aum;
        this.expenseRatio = expenseRatio;
        this.inceptionDate = inceptionDate;
        this.averageVol = averageVol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public BigDecimal getAum() {
        return aum;
    }

    public void setAum(BigDecimal aum) {
        this.aum = aum;
    }

    public BigDecimal getExpenseRatio() {
        return expenseRatio;
    }

    public void setExpenseRatio(BigDecimal expenseRatio) {
        this.expenseRatio = expenseRatio;
    }

    public LocalDate getInceptionDate() {
        return inceptionDate;
    }

    public void setInceptionDate(LocalDate inceptionDate) {
        this.inceptionDate = inceptionDate;
    }

    public BigDecimal getAverageVol() {
        return averageVol;
    }

    public void setAverageVol(BigDecimal averageVol) {
        this.averageVol = averageVol;
    }

    @Override
    public int compareTo(Fund o) {
        return symbol.compareTo(o.getSymbol());
    }

    @Override
    public String toString() {
        return "Fund{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

//    @Override
//    public String toString() {
//        return "Fund{" +
//                "symbol='" + symbol + '\'' +
//                ", name='" + name + '\'' +
//                ", category='" + category + '\'' +
//                ", provider='" + provider + '\'' +
//                '}';
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fund fund = (Fund) o;

        if (category != null ? !category.equals(fund.category) : fund.category != null) return false;
        if (inceptionDate != null ? !inceptionDate.equals(fund.inceptionDate) : fund.inceptionDate != null)
            return false;
        if (name != null ? !name.equals(fund.name) : fund.name != null) return false;
        if (provider != null ? !provider.equals(fund.provider) : fund.provider != null) return false;
        if (!symbol.equals(fund.symbol)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = symbol.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (provider != null ? provider.hashCode() : 0);
        result = 31 * result + (inceptionDate != null ? inceptionDate.hashCode() : 0);
        return result;
    }
}
