package com.mns.mojoinvest.server.engine.model;

import java.io.Serializable;

public class RankingParams implements Serializable {

    private int formationPeriod;

    public RankingParams(int formationPeriod) {
        this.formationPeriod = formationPeriod;
    }

    public int getFormationPeriod() {
        return formationPeriod;
    }

    @Override
    public String toString() {
        return formationPeriod + "";
    }
}
