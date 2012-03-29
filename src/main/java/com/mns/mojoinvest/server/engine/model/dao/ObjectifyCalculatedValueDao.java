package com.mns.mojoinvest.server.engine.model.dao;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyFactory;
import com.mns.mojoinvest.server.engine.model.CalculatedValue;
import com.mns.mojoinvest.server.engine.model.Fund;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.mns.mojoinvest.server.util.DatastoreUtils.forDatastore;

public class ObjectifyCalculatedValueDao extends DAOBase implements CalculatedValueDao {


    private static final Logger log = Logger.getLogger(ObjectifyCalculatedValueDao.class.getName());

    private static boolean objectsRegistered;

    @Inject
    public ObjectifyCalculatedValueDao(final ObjectifyFactory objectifyFactory) {
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


    @Override
    public Key<CalculatedValue> put(CalculatedValue cv) {
        return ofy().put(cv);
    }

    @Override
    public Map<Key<CalculatedValue>, CalculatedValue> put(List<CalculatedValue> cvs) {
        return ofy().put(cvs);
    }

    @Override
    public Collection<CalculatedValue> get(List<LocalDate> dates, Collection<Fund> funds, String type, int period) {
        List<Key<CalculatedValue>> keys = new ArrayList<Key<CalculatedValue>>();
        for (LocalDate date : dates) {
            for (Fund fund : funds) {
                keys.add(new Key<CalculatedValue>(CalculatedValue.class, calculatedValueId(date, fund.getSymbol(), type, period)));
            }
        }
        return ofy().get(keys).values();
    }

    @Override
    public Collection<CalculatedValue> get(LocalDate date, Collection<Fund> funds,
                                           String type, int period) {
        List<Key<CalculatedValue>> keys = new ArrayList<Key<CalculatedValue>>();
        for (Fund fund : funds) {
            keys.add(new Key<CalculatedValue>(CalculatedValue.class, calculatedValueId(date, fund.getSymbol(), type, period)));
        }
        return ofy().get(keys).values();
    }

    public static String calculatedValueId(LocalDate date, String symbol, String type, int period) {
        return forDatastore(date) + "|" + symbol + "|" + type + "|" + period;
    }
}
