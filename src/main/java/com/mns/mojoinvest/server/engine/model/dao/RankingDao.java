package com.mns.mojoinvest.server.engine.model.dao;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.Ranking;
import com.mns.mojoinvest.server.engine.model.RankingParams;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public void registerObjects(ObjectifyFactory ofyFactory) {
        objectsRegistered = true;
        ofyFactory.register(Ranking.class);
    }

    public Ranking get(LocalDate date, RankingParams params) {
        Key<Ranking> key = new Key<Ranking>(Ranking.class, Ranking.createId(date, params));
        return ofy().get(key);
    }

    public List<Ranking> get(List<LocalDate> dates, RankingParams params) {
        List<Key<Ranking>> keys = new ArrayList<Key<Ranking>>(dates.size());
        for (LocalDate date : dates) {
            keys.add(new Key<Ranking>(Ranking.class, Ranking.createId(date, params)));
        }
        return new ArrayList<Ranking>(ofy().get(keys).values());
    }

    public Key<Ranking> put(Ranking ranking) {
        return ofy().put(ranking);
    }

    public Map<Key<Ranking>, Ranking> put(List<Ranking> rankings) {
        return ofy().put(rankings);
    }

    public Map<Key<Ranking>, Ranking> put(Iterable<Ranking> rankings) {
        return ofy().put(rankings);
    }

}
