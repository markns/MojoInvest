package com.mns.mojoinvest.shared.params;

public class Strategy2Params {
    private final int portfolioSize;
    private final int rebalanceFrequency;
    private final int ma1;
    private final int ma2;
    private final int roc;
    private final int castOff;
    private final int stddev;
    private final boolean equityCurveTrading;
    private final int equityCurveWindow;
    private String relativeStrengthStyle;
    private boolean useSafeAsset;
    private String safeAsset;

    public Strategy2Params(int portfolioSize, int rebalanceFrequency, int ma1, int ma2, int roc,
                           int castOff, int stddev, boolean equityCurveTrading, int equityCurveWindow,
                           String relativeStrengthStyle, boolean useSafeAsset, String safeAsset) {

        this.portfolioSize = portfolioSize;
        this.rebalanceFrequency = rebalanceFrequency;
        this.ma1 = ma1;
        this.ma2 = ma2;
        this.roc = roc;
        this.castOff = castOff;
        this.stddev = stddev;
        this.equityCurveTrading = equityCurveTrading;
        this.equityCurveWindow = equityCurveWindow;
        this.relativeStrengthStyle = relativeStrengthStyle;
        this.useSafeAsset = useSafeAsset;
        this.safeAsset = safeAsset;
    }

    public int getPortfolioSize() {
        return portfolioSize;
    }

    public int getRebalanceFrequency() {
        return rebalanceFrequency;
    }

    public int getMa1() {
        return ma1;
    }

    public int getMa2() {
        return ma2;
    }

    public int getRoc() {
        return roc;
    }

    public int getCastOff() {
        return castOff;
    }

    public int getStdDev() {
        return stddev;
    }

    public boolean tradeEquityCurve() {
        return equityCurveTrading;
    }

    public int getEquityCurveWindow() {
        return equityCurveWindow;
    }

    public String getRelativeStrengthStyle() {
        return relativeStrengthStyle;
    }

    public boolean useSafeAsset() {
        return useSafeAsset;
    }

    public String getSafeAsset() {
        return safeAsset;
    }

    @Override
    public String toString() {
        return "Params: {" +
                "portfolioSize=" + portfolioSize +
                ", rebalanceFrequency=" + rebalanceFrequency +
                ", ma1=" + ma1 +
                ", ma2=" + ma2 +
                ", roc=" + roc +
                ", castOff=" + castOff +
                ", stddev=" + stddev +
                ", equityCurveTrading=" + equityCurveTrading +
                ", equityCurveWindow=" + equityCurveWindow +
                ", relativeStrengthStyle='" + relativeStrengthStyle + '\'' +
                ", useSafeAsset=" + useSafeAsset +
                ", safeAsset='" + safeAsset + '\'' +
                '}';
    }
}
