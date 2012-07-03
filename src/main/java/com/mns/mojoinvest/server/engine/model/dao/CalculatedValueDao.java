package com.mns.mojoinvest.server.engine.model.dao;

import com.mns.mojoinvest.server.engine.model.Fund;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public interface CalculatedValueDao {

    Map<String, Map<LocalDate, BigDecimal>> get(Collection<Fund> funds, String type, int period);

}
