package com.mns.alphaposition.server.engine.model;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.alphaposition.server.model.DAOBase;
import org.joda.time.LocalDate;

public class RankingDao extends DAOBase {

    private static boolean objectsRegistered;

    @Inject
    public RankingDao(final ObjectifyFactory objectifyFactory) {
        super(objectifyFactory);
    }

    @Override
    protected boolean areObjectsRegistered() {
        return objectsRegistered;
    }

    @Override
    protected void registerObjects(ObjectifyFactory ofyFactory) {
        objectsRegistered = true;
        ofyFactory.register(Ranking.class);
    }

    public Ranking get(LocalDate date) {
        return get(new Key<Ranking>(Ranking.class, date.toString()));
    }

    public Ranking get(Key<Ranking> key) {
        return ofy().get(key);
    }

}
