package com.mns.mojoinvest.server.engine.model.dao;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.Fund;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface FundDao {

    void registerObjects(ObjectifyFactory ofyFactory);

    Collection<Fund> list();

    Fund get(String symbol);

    Collection<Fund> get(Collection<String> symbols);

    void put(Fund fund);

    Map<Key<Fund>, Fund> put(Set<Fund> funds);

}
