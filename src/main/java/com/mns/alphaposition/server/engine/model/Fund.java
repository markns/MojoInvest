package com.mns.alphaposition.server.engine.model;

import com.googlecode.objectify.annotation.Cached;
import org.joda.time.LocalDate;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Cached
public class Fund implements Comparable<Fund> {

    @Id
    private String symbol;

    private String name;

    private String category;

    private String provider;

    private boolean active;

    private String country;

    private String index;

    private String overview;

    private LocalDate inceptionDate;

    //TODO: Update this each time the fund is updated from msn
    private Date lastUpdated;

//    private BigDecimal aum;
//
//    private BigDecimal expenseRatio;
//
//    private BigDecimal averageVol;

    public Fund() {
        //No arg for objectify
    }

    public Fund(String symbol, String name, String category, String provider, boolean active, String country,
                String index, String overview, LocalDate inceptionDate) {
        this.symbol = symbol;
        this.name = name;
        this.category = category;
        this.provider = provider;
        this.active = active;
        this.country = country;
        this.index = index;
        this.overview = overview;
        this.inceptionDate = inceptionDate;
        this.lastUpdated = new Date();
    }

    @Deprecated
    public Fund(String symbol, String name, String category, String provider, BigDecimal aum,
                BigDecimal expenseRatio, LocalDate inceptionDate, BigDecimal averageVol) {
        this.symbol = symbol;
        this.name = name;
        this.category = category;
        this.provider = provider;
//        this.aum = aum;
//        this.expenseRatio = expenseRatio;
        this.inceptionDate = inceptionDate;
//        this.averageVol = averageVol;
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

    public LocalDate getInceptionDate() {
        return inceptionDate;
    }

    public void setInceptionDate(LocalDate inceptionDate) {
        this.inceptionDate = inceptionDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
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
                ", category='" + category + '\'' +
                ", provider='" + provider + '\'' +
                ", inceptionDate=" + inceptionDate +
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

    public String toCsv() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
