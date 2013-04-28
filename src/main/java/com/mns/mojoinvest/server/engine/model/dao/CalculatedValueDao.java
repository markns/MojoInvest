package com.mns.mojoinvest.server.engine.model.dao;

import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CalculatedValueDao {

    Map<String, Map<LocalDate, CalculatedValue>> get(Collection<Fund> funds, String type, int period);

    void put(String key, List<CalculatedValue> cvs);
}
