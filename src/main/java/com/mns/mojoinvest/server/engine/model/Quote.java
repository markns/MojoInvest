package com.mns.mojoinvest.server.engine.model;

import com.mns.mojoinvest.server.util.QuoteUtils;
import org.joda.time.LocalDate;

import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

public class Quote implements Serializable {

    @Id
    private String id;

    private String symbol;

    private LocalDate date;

    private BigDecimal index;

    private BigDecimal nav;

    private BigDecimal trNav;

    private BigDecimal dividend;

    private boolean rolled = false;

    public Quote(String symbol, LocalDate date, BigDecimal index, BigDecimal nav,
                 BigDecimal trNav, BigDecimal dividend, boolean rolled) {
        this.id = QuoteUtils.quoteId(symbol, date);
        this.symbol = symbol;
        this.date = date;
        this.nav = nav;
        this.index = index;
        this.trNav = trNav;
        this.dividend = dividend;
        this.rolled = rolled;
    }

    public String getSymbol() {
        return symbol;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getIndex() {
        return index;
    }

    public BigDecimal getNav() {
        return nav;
    }

    public BigDecimal getTrNav() {
        return trNav;
//        return nav;
    }

    public BigDecimal getDividend() {
        return dividend;
    }

    public boolean isRolled() {
        return rolled;
    }

    @Override
    public String toString() {
        return id;
    }

    public String toDescriptiveString() {
        return "Quote{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", date='" + date + '\'' +
                ", nav=" + nav +
                ", trNav=" + trNav +
                ", divdend=" + dividend +
                ", rolled=" + rolled +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Quote quote = (Quote) o;

        if (rolled != quote.rolled) return false;
        if (dividend != null ? !dividend.equals(quote.dividend) : quote.dividend != null) return false;
        if (nav != null ? !nav.equals(quote.nav) : quote.nav != null) return false;
        if (date != null ? !date.equals(quote.date) : quote.date != null) return false;
        if (id != null ? !id.equals(quote.id) : quote.id != null) return false;
        if (symbol != null ? !symbol.equals(quote.symbol) : quote.symbol != null) return false;
        if (trNav != null ? !trNav.equals(quote.trNav) : quote.trNav != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (nav != null ? nav.hashCode() : 0);
        result = 31 * result + (trNav != null ? trNav.hashCode() : 0);
        result = 31 * result + (dividend != null ? dividend.hashCode() : 0);
        result = 31 * result + (rolled ? 1 : 0);
        return result;
    }


    public String[] toStrArr() {
        return new String[]{
                symbol,
                date + "",
                nav + "",
                index + "",
                trNav + "",
                dividend + "",
                rolled + ""};

    }

}