package com.mns.alphaposition.server.engine.model;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Unindexed;
import com.mns.alphaposition.server.util.QuoteUtils;
import org.joda.time.LocalDate;

import javax.persistence.Id;
import java.math.BigDecimal;

@Cached
public class Quote {

    @Id
    private String id;

    private String symbol;

    private LocalDate date;

    @Unindexed
    private BigDecimal open;

    @Unindexed
    private BigDecimal high;

    @Unindexed
    private BigDecimal low;

    @Unindexed
    private BigDecimal close;

    @Unindexed
    private BigDecimal bid;

    @Unindexed
    private BigDecimal ask;

    @Unindexed
    private BigDecimal volume;

    @Unindexed
    private BigDecimal adjClose;

    @Unindexed
    private boolean rolled = false;

    public Quote() {
        //no arg for objectify
    }

    public Quote(String symbol, LocalDate date, BigDecimal open, BigDecimal high, BigDecimal low,
                 BigDecimal close, BigDecimal bid, BigDecimal ask,
                 BigDecimal volume, BigDecimal adjClose, boolean rolled) {
        this.id = QuoteUtils.quoteId(symbol, date);
        this.symbol = symbol;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.bid = bid;
        this.ask = ask;
        this.volume = volume;
        this.adjClose = adjClose;
        this.rolled = rolled;
    }

    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getAdjClose() {
        return adjClose;
    }

    public void setAdjClose(BigDecimal adjClose) {
        this.adjClose = adjClose;
    }

    public boolean isRolled() {
        return rolled;
    }

    public void setRolled(boolean rolled) {
        this.rolled = rolled;
    }


    @Override
    public String toString() {
        return "Quote{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", date='" + date + '\'' +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                ", adjClose=" + adjClose +
                ", rolled=" + rolled +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Quote quote = (Quote) o;

        if (rolled != quote.rolled) return false;
        if (adjClose != null ? !adjClose.equals(quote.adjClose) : quote.adjClose != null) return false;
        if (close != null ? !close.equals(quote.close) : quote.close != null) return false;
        if (date != null ? !date.equals(quote.date) : quote.date != null) return false;
        if (high != null ? !high.equals(quote.high) : quote.high != null) return false;
        if (id != null ? !id.equals(quote.id) : quote.id != null) return false;
        if (low != null ? !low.equals(quote.low) : quote.low != null) return false;
        if (open != null ? !open.equals(quote.open) : quote.open != null) return false;
        if (symbol != null ? !symbol.equals(quote.symbol) : quote.symbol != null) return false;
        if (volume != null ? !volume.equals(quote.volume) : quote.volume != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (open != null ? open.hashCode() : 0);
        result = 31 * result + (high != null ? high.hashCode() : 0);
        result = 31 * result + (low != null ? low.hashCode() : 0);
        result = 31 * result + (close != null ? close.hashCode() : 0);
        result = 31 * result + (volume != null ? volume.hashCode() : 0);
        result = 31 * result + (adjClose != null ? adjClose.hashCode() : 0);
        result = 31 * result + (rolled ? 1 : 0);
        return result;
    }
}