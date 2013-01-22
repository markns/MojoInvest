package com.mns.mojoinvest.server.engine.model;

import com.googlecode.objectify.annotation.Cached;
import com.mns.mojoinvest.server.serialization.CustomLocalDateSerializer;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;

import javax.persistence.Id;
import java.io.Serializable;

@Cached
public class Fund implements Serializable, Comparable<Fund> {

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

    private LocalDate earliestQuoteDate;

    private LocalDate latestQuoteDate;
    private String fundId;

//    private BigDecimal aum;
//
//    private BigDecimal expenseRatio;
//
//    private BigDecimal averageVol;

    public Fund() {
        //No arg for objectify
    }

    public Fund(String symbol, String fundId, String name, String category, String provider, boolean active, String country,
                String index, String overview, LocalDate inceptionDate) {
        this.symbol = symbol;
        this.fundId = fundId;
        this.name = name;
        this.category = category;
        this.provider = provider;
        this.active = active;
        this.country = country;
        this.index = index;
        this.overview = overview;
        this.inceptionDate = inceptionDate;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getFundId() {
        return fundId;
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

    @JsonSerialize(using = CustomLocalDateSerializer.class)
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

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getEarliestQuoteDate() {
        return earliestQuoteDate;
    }

    public void setEarliestQuoteDate(LocalDate earliestQuoteDate) {
        this.earliestQuoteDate = earliestQuoteDate;
    }

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getLatestQuoteDate() {
        return latestQuoteDate;
    }

    public void setLatestQuoteDate(LocalDate latestQuoteDate) {
        this.latestQuoteDate = latestQuoteDate;
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
                ", active=" + active +
                ", country='" + country + '\'' +
                ", index='" + index + '\'' +
                ", inceptionDate=" + inceptionDate +
                ", latestQuoteDate=" + latestQuoteDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fund fund = (Fund) o;

        if (!symbol.equals(fund.symbol)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }

    public static Fund fromStrArr(String[] arr) {
        return new Fund(arr[0], "",
                arr[1],
                arr[2],
                arr[3],
                Boolean.parseBoolean(arr[4]),
                arr[5],
                arr[6],
                arr[7],
                new LocalDate(arr[8])
        );
    }

    public String[] toStrArr() {
        return new String[]{
                symbol,
                name,
                category,
                provider,
                active + "",
                country,
                index,
                overview,
                inceptionDate + ""
        };
    }
}
