package com.mns.mojoinvest.server.engine.model.dao.objectify;

import com.googlecode.objectify.impl.conv.Converter;
import com.googlecode.objectify.impl.conv.ConverterLoadContext;
import com.googlecode.objectify.impl.conv.ConverterSaveContext;
import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Date;

//TODO: Split this into BigDecimalTypeConverter and JodaTimeTypeConverters
public class MyTypeConverters implements Converter {

    @Override
    public Object forPojo(Object value, Class<?> fieldType, ConverterLoadContext
            ctx, Object onPojo) {

        if (fieldType == BigDecimal.class && value instanceof String) {
            //TODO: shouldn't be converting null values to Zero, but objectify throws
            //an exception if a null value is returned.
            if (((String) value).isEmpty()) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal((String) value);
        }
        if (fieldType == DateMidnight.class && value instanceof Date)
            return new DateMidnight(value);
        if (fieldType == LocalDate.class && value instanceof Date)
            return new LocalDate(value);
        else
            return null;
    }

    @Override
    public Object forDatastore(Object value, ConverterSaveContext
            ctx) {
        if (value instanceof BigDecimal)
            return value.toString();
        if (value instanceof DateMidnight)
            return ((DateMidnight) value).toDate();
        if (value instanceof LocalDate)
            return ((LocalDate) value).toDate();
        else
            return null;
    }

}
