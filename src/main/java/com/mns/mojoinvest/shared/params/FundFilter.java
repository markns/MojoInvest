package com.mns.mojoinvest.shared.params;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.HashSet;
import java.util.Set;

public class FundFilter implements IsSerializable {

    private Set<String> providers = new HashSet<String>();

    private Set<String> categories = new HashSet<String>();


    public FundFilter(Set<String> providers, Set<String> categories) {
        this.providers = providers;
        this.categories = categories;
    }

    public FundFilter() {
    }

    public Set<String> getProviders() {
        return providers;
    }

    public Set<String> getCategories() {
        return categories;
    }

    @Override
    public String toString() {
        return "FundFilter{" +
                "providers=" + providers.size() +
                ", categories=" + categories.size() +
                '}';
    }
}
