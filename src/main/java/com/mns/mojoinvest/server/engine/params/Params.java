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
    private int minHoldingPeriod;
    private int ma1;
    private int ma2;
    private int roc;
    private int alpha;
    private int castOff;
    private boolean riskAdjusted;
    private int stdDev;
    private boolean tradeEquityCurve;
    private int equityCurveWindow;
    private String relativeStrengthStyle;
    private boolean useSafeAsset;
    private String safeAsset;
    private List<String> universe;

    public Params(LocalDate fromDate, LocalDate toDate, LocalDate creationDate, Double initialInvestment,
                  Double transactionCost, int portfolioSize, int rebalanceFrequency, int minHoldingPeriod, int ma1, int ma2,
                  int roc, int alpha, int castOff, boolean riskAdjusted, int stdDev, boolean tradeEquityCurve,
                  int equityCurveWindow, String relativeStrengthStyle, boolean useSafeAsset,
                  String safeAsset, List<String> universe) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.creationDate = creationDate;
        this.initialInvestment = initialInvestment;
        this.transactionCost = transactionCost;
        this.portfolioSize = portfolioSize;
        this.rebalanceFrequency = rebalanceFrequency;
        this.minHoldingPeriod = minHoldingPeriod;
        this.ma1 = ma1;
        this.ma2 = ma2;
        this.roc = roc;
        this.alpha = alpha;
        this.castOff = castOff;
        this.riskAdjusted = riskAdjusted;
        this.stdDev = stdDev;
        this.tradeEquityCurve = tradeEquityCurve;
        this.equityCurveWindow = equityCurveWindow;
        this.relativeStrengthStyle = relativeStrengthStyle;
        this.useSafeAsset = useSafeAsset;
        this.safeAsset = safeAsset;
        this.universe = universe;
    }

    public Params() {
        //Constructor for Jackson serialization
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

    public int getMinHoldingPeriod() {
        return minHoldingPeriod;
    }

    public void setMinHoldingPeriod(int minHoldingPeriod) {
        this.minHoldingPeriod = minHoldingPeriod;
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

    public boolean isRiskAdjusted() {
        return riskAdjusted;
    }

    public void setRiskAdjusted(boolean riskAdjusted) {
        this.riskAdjusted = riskAdjusted;
    }

    public int getStdDev() {
        return stdDev;
    }

    public void setStdDev(int stdDev) {
        this.stdDev = stdDev;
    }

    public boolean isTradeEquityCurve() {
        return tradeEquityCurve;
    }

    public void setTradeEquityCurve(boolean tradeEquityCurve) {
        this.tradeEquityCurve = tradeEquityCurve;
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

    @Override
    public String toString() {
        return "Params{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", creationDate=" + creationDate +
                ", initialInvestment=" + initialInvestment +
                ", transactionCost=" + transactionCost +
                ", portfolioSize=" + portfolioSize +
                ", rebalanceFrequency=" + rebalanceFrequency +
                ", minHoldingPeriod=" + minHoldingPeriod +
                ", ma1=" + ma1 +
                ", ma2=" + ma2 +
                ", roc=" + roc +
                ", alpha=" + alpha +
                ", castOff=" + castOff +
                ", riskAdjusted=" + riskAdjusted +
                ", stdDev=" + stdDev +
                ", tradeEquityCurve=" + tradeEquityCurve +
                ", equityCurveWindow=" + equityCurveWindow +
                ", relativeStrengthStyle='" + relativeStrengthStyle + '\'' +
                ", useSafeAsset=" + useSafeAsset +
                ", safeAsset='" + safeAsset + '\'' +
                ", universe=" + universe +
                '}';
    }
}
