package com.mns.mojoinvest.client;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation is used in {@link com.mns.mojoinvest.client.MainPlaceManager} and is bind
 * in {@link MainModule}. It's purpose is to bind the default place to a
 * default presenter.
 */
@BindingAnnotation
@Target({FIELD, PARAMETER, METHOD})
@Retention(RUNTIME)
public @interface DefaultPlace {
}