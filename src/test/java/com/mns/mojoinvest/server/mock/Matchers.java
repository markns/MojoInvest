package com.mns.mojoinvest.server.mock;

import com.mns.mojoinvest.server.engine.model.Fund;
import org.joda.time.LocalDate;
import org.mockito.ArgumentMatcher;

import static org.mockito.Matchers.argThat;

public class Matchers {

    //TODO: Move to shared class
    public static LocalDate anyLocalDate() {
        return argThat(new IsLocalDate());
    }

    public static Fund anyFund() {
        return argThat(new IsFund());
    }

    public static class IsFund extends ArgumentMatcher<Fund> {
        @Override
        public boolean matches(Object argument) {
            return argument instanceof Fund;
        }
    }

    public static class IsLocalDate extends ArgumentMatcher<LocalDate> {
        @Override
        public boolean matches(Object argument) {
            return argument instanceof LocalDate;
        }
    }

}
