package com.mns.mojoinvest.server.engine.model;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.Id;
import java.util.Set;

@Cached
@Unindexed
@Entity
public class ProviderSet {

    @Id
    private String id = "providers";

    private Set<String> providers;

    public ProviderSet() {
    }

    public ProviderSet(Set<String> providers) {
        this.providers = providers;
    }

    public Set<String> getProviders() {
        return providers;
    }
}
