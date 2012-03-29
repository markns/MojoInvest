package com.mns.mojoinvest.server.engine.model.dao;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface FundDao {

    void registerObjects(ObjectifyFactory ofyFactory);

    Collection<Fund> list();

    Collection<Fund> getAll();

    Fund get(String symbol);

    Collection<Fund> get(Collection<String> symbols);

    Set<String> getByCategory(String categoryName);

    Set<String> getByProvider(String providerName);

    Map<Key<Fund>, Fund> put(Set<Fund> funds);

    Key<Symbols> put(Symbols symbols);

    void put(ProviderSet providerSet);

    Map<Key<Provider>, Provider> putProviders(Collection<Provider> providers);

    Key<CategorySet> put(CategorySet categorySet);

    Map<Key<Category>, Category> putCategories(Collection<Category> values);

    Set<String> getProviderSet();

    Set<String> getCategorySet();
}
