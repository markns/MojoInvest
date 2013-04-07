package com.mns.mojoinvest.server.engine.model.dao;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.Correlation;
import com.mns.mojoinvest.server.engine.model.dao.objectify.DAOBase;
import com.mns.mojoinvest.server.engine.model.dao.objectify.MyTypeConverters;
import com.mns.mojoinvest.server.engine.model.dao.objectify.ObjectifyFundDao;
import org.joda.time.LocalDate;

import java.util.logging.Logger;

public class ObjectifyCorrelationDao extends DAOBase implements CorrelationDao {

    private static final Logger log = Logger.getLogger(ObjectifyFundDao.class.getName());

    private static boolean objectsRegistered;

    @Inject
    public ObjectifyCorrelationDao(final ObjectifyFactory objectifyFactory) {
        super(objectifyFactory);
    }

    @Override
    protected boolean areObjectsRegistered() {
        return objectsRegistered;
    }

    @Override
    public void registerObjects(ObjectifyFactory ofyFactory) {
        objectsRegistered = true;
        ofyFactory.register(Correlation.class);
        ofyFactory.getConversions().add(new MyTypeConverters());
    }

    @Override
    public void save(Correlation correlation) {
        ofy().put(correlation);
    }

    @Override
    public Correlation get(LocalDate date, int period) {
        Key<Correlation> key = new Key<Correlation>(Correlation.class, date + "|" + period);
        return ofy().get(key);
    }
}
