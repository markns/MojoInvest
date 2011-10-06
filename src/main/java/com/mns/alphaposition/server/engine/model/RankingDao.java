package com.mns.alphaposition.server.engine.model;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.Query;
import com.mns.alphaposition.server.model.DAOBase;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
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
    protected void registerObjects(ObjectifyFactory ofyFactory) {
        objectsRegistered = true;
        ofyFactory.register(Ranking.class);
        ofyFactory.register(RankingText.class);
    }

    public Collection<Ranking> get(List<LocalDate> dates) {
        List<Key<Ranking>> keys = new ArrayList<Key<Ranking>>(dates.size());
        for (LocalDate date : dates) {
            keys.add(new Key<Ranking>(Ranking.class, date.toString()));
        }
        return ofy().get(keys).values();
    }

    public Ranking get(LocalDate date) {
        return get(new Key<Ranking>(Ranking.class, date.toString()));
    }

    public Ranking get(Key<Ranking> key) {
        return ofy().get(key);
    }

    public List<Ranking> list() {
        Query<Ranking> q = ofy().query(Ranking.class);
        return q.list();
    }

    public Map<Key<RankingText>, RankingText> put(List<RankingText> rankingTexts) {
        return ofy().put(rankingTexts);
    }

    public Collection<RankingText> getRankingText(List<LocalDate> dates) {
        List<Key<RankingText>> keys = new ArrayList<Key<RankingText>>(dates.size());
        for (LocalDate date : dates) {
            keys.add(new Key<RankingText>(RankingText.class, date.toString()));
        }
        return ofy().get(keys).values();
    }


}
