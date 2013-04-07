package com.mns.mojoinvest.server.engine.model.dao;

import com.mns.mojoinvest.server.engine.model.Correlation;
import org.joda.time.LocalDate;

public interface CorrelationDao {


    void save(Correlation correlation);

    Correlation get(LocalDate date, int period);
}
