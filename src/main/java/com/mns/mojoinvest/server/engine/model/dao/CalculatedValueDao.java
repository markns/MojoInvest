package com.mns.mojoinvest.server.engine.model.dao;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CalculatedValueDao {

    void registerObjects(ObjectifyFactory ofyFactory);

    Key<CalculatedValue> put(CalculatedValue cv);

    Map<Key<CalculatedValue>, CalculatedValue> put(List<CalculatedValue> cvs);

    Collection<CalculatedValue> get(List<LocalDate> dates, Collection<Fund> funds, String type, int period);

    Collection<CalculatedValue> get(LocalDate date, Collection<Fund> funds,
                                    String type, int period);
}
