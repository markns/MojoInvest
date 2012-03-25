package com.mns.mojoinvest.server.engine.model.dao;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;

import java.util.logging.Logger;

public class CalculatedValueDao extends DAOBase {


    private static final Logger log = Logger.getLogger(CalculatedValueDao.class.getName());

    private static boolean objectsRegistered;

    @Inject
    public CalculatedValueDao(final ObjectifyFactory objectifyFactory) {
        super(objectifyFactory);
    }

    @Override
    protected boolean areObjectsRegistered() {
        return objectsRegistered;
    }

    @Override
    public void registerObjects(ObjectifyFactory ofyFactory) {
        objectsRegistered = true;
        ofyFactory.register(CalculatedValue.class);
        ofyFactory.getConversions().add(new MyTypeConverters());
    }


    public Key<CalculatedValue> put(CalculatedValue cv) {
        return ofy().put(cv);
    }
}
