package com.mns.mojoinvest.server.engine.params;


import com.mns.mojoinvest.server.serialization.CustomLocalDateDeserializer;
import com.mns.mojoinvest.server.serialization.CustomLocalDateSerializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;

import java.util.List;

public class Params {

    private LocalDate fromDate;
    private LocalDate toDate;
    private LocalDate creationDate;
    private Double initialInvestment;
    private Double transactionCost;
    private int portfolioSize;
    private int rebalanceFrequency;
    private int ma1;
    private int ma2;
    private int roc;
    private int alpha;
    private int castOff;
    private boolean riskAdjust;
    private int stddev;
    private boolean equityCurveTrading;
    private int equityCurveWindow;
    private String relativeStrengthStyle;
    private boolean useSafeAsset;
    private String safeAsset;
    private List<String> universe;

    public Params(LocalDate fromDate, LocalDate toDate, LocalDate creationDate, Double initialInvestment,
                  Double transactionCost, int portfolioSize, int rebalanceFrequency, int ma1, int ma2,
                  int roc, int alpha, int castOff, boolean riskAdjust, int stddev, boolean equityCurveTrading,
                  int equityCurveWindow, String relativeStrengthStyle, boolean useSafeAsset,
                  String safeAsset, List<String> universe) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.creationDate = creationDate;
        this.initialInvestment = initialInvestment;
        this.transactionCost = transactionCost;
        this.portfolioSize = portfolioSize;
        this.rebalanceFrequency = rebalanceFrequency;
        this.ma1 = ma1;
        this.ma2 = ma2;
        this.roc = roc;
        this.alpha = alpha;
        this.castOff = castOff;
        this.riskAdjust = riskAdjust;
        this.stddev = stddev;
        this.equityCurveTrading = equityCurveTrading;
        this.equityCurveWindow = equityCurveWindow;
        this.relativeStrengthStyle = relativeStrengthStyle;
        this.useSafeAsset = useSafeAsset;
        this.safeAsset = safeAsset;
        this.universe = universe;
    }

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getFromDate() {
        return fromDate;
    }

    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getToDate() {
        return toDate;
    }

    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    public LocalDate getCreationDate() {
        return creationDate;
    }

    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Double getInitialInvestment() {
        return initialInvestment;
    }

    public void setInitialInvestment(Double initialInvestment) {
        this.initialInvestment = initialInvestment;
    }

    public Double getTransactionCost() {
        return transactionCost;
    }

    public void setTransactionCost(Double transactionCost) {
        this.transactionCost = transactionCost;
    }

    public int getPortfolioSize() {
        return portfolioSize;
    }

    public void setPortfolioSize(int portfolioSize) {
        this.portfolioSize = portfolioSize;
    }

    public int getRebalanceFrequency() {
        return rebalanceFrequency;
    }

    public void setRebalanceFrequency(int rebalanceFrequency) {
        this.rebalanceFrequency = rebalanceFrequency;
    }

    public int getMa1() {
        return ma1;
    }

    public void setMa1(int ma1) {
        this.ma1 = ma1;
    }

    public int getMa2() {
        return ma2;
    }

    public void setMa2(int ma2) {
        this.ma2 = ma2;
    }

    public int getRoc() {
        return roc;
    }

    public void setRoc(int roc) {
        this.roc = roc;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getCastOff() {
        return castOff;
    }

    public void setCastOff(int castOff) {
        this.castOff = castOff;
    }

    public boolean isRiskAdjust() {
        return riskAdjust;
    }

    public void setRiskAdjust(boolean riskAdjust) {
        this.riskAdjust = riskAdjust;
    }

    public int getStddev() {
        return stddev;
    }

    public void setStddev(int stddev) {
        this.stddev = stddev;
    }

    public boolean isEquityCurveTrading() {
        return equityCurveTrading;
    }

    public void setEquityCurveTrading(boolean equityCurveTrading) {
        this.equityCurveTrading = equityCurveTrading;
    }

    public int getEquityCurveWindow() {
        return equityCurveWindow;
    }

    public void setEquityCurveWindow(int equityCurveWindow) {
        this.equityCurveWindow = equityCurveWindow;
    }

    public String getRelativeStrengthStyle() {
        return relativeStrengthStyle;
    }

    public void setRelativeStrengthStyle(String relativeStrengthStyle) {
        this.relativeStrengthStyle = relativeStrengthStyle;
    }

    public boolean isUseSafeAsset() {
        return useSafeAsset;
    }

    public void setUseSafeAsset(boolean useSafeAsset) {
        this.useSafeAsset = useSafeAsset;
    }

    public String getSafeAsset() {
        return safeAsset;
    }

    public void setSafeAsset(String safeAsset) {
        this.safeAsset = safeAsset;
    }

    public List<String> getUniverse() {
        return universe;
    }

    public void setUniverse(List<String> universe) {
        this.universe = universe;
    }
}
