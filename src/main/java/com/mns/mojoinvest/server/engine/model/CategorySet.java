package com.mns.mojoinvest.server.engine.model;

import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.Id;
import java.util.Set;

@Cached
@Unindexed
@Entity
public class CategorySet {

    @Id
    private String id = "categories";

    private Set<String> categories;

    public CategorySet() {
    }

    public CategorySet(Set<String> categories) {
        this.categories = categories;
    }

    public Set<String> getCategories() {
        return categories;
    }
}
