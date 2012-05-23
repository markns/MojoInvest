package com.mns.mojoinvest.server.guice;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.mns.mojoinvest.server.mustachelet.App;
import com.mns.mojoinvest.server.mustachelet.Index;
import com.mns.mojoinvest.server.mustachelet.Post;

import java.util.List;

public class MustacheModule extends AbstractModule {

    @Override
    protected void configure() {
        binder().bind(new TypeLiteral<List<Class<?>>>() {
        }).toInstance(Lists.newArrayList(Index.class, Post.class, App.class));
    }
}
