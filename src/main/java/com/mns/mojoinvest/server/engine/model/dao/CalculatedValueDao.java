package com.mns.mojoinvest.server.engine.model.dao;

import com.googlecode.objectify.Key;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CalculatedValueDao {

    Key<CalculatedValue> put(CalculatedValue cv);

    Map<Key<CalculatedValue>, CalculatedValue> put(List<CalculatedValue> cvs);

    Map<String, Map<LocalDate, BigDecimal>> get(Collection<Fund> funds, String type, int period);

}
