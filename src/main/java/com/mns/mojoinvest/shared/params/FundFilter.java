package com.mns.mojoinvest.shared.params;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

public class FundFilter implements IsSerializable {

    private List<String> providers = new ArrayList<String>();

    private List<String> categories = new ArrayList<String>();


    public FundFilter(List<String> providers, List<String> categories) {
        this.providers = providers;
        this.categories = categories;
    }

    public FundFilter() {
    }

    public List<String> getProviders() {
        return providers;
    }

    public List<String> getCategories() {
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
