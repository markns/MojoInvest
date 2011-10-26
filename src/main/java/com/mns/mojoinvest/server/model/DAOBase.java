/**
 * Copyright 2010 Mark Nuttall-Smith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mns.mojoinvest.server.model;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;

/**
 * Inspired by {@link com.googlecode.objectify.helper.DAOBase}, but modified to
 * better support dependency injection.
 *
 * @author Phillipe Beaudoin
 * @author Jeff Schnitzer
 */
public abstract class DAOBase {

    private final ObjectifyFactory factory;

    /**
     * A single objectify interface, lazily created.
     */
    private Objectify lazyOfy;

    /**
     * Creates the DAO, injecting the {@link ObjectifyFactory}.
     *
     * @param factory The injected factory.
     */
    public DAOBase(final ObjectifyFactory factory) {
        this.factory = factory;

        ensureObjectsAreRegistered();
    }

    /**
     * Ensures the objects controlled by this DAO are registered
     * towards objectify exactly once. The method is synchronized
     * for thread safety.
     */
    private synchronized void ensureObjectsAreRegistered() {
        if (areObjectsRegistered()) {
            return;
        }
        registerObjects(factory);
    }

    /**
     * Override this method to check whether the objects controlled by
     * this DAO have been registered (exactly once). To implement this
     * method, you should declare a field:
     * <pre>
     *   private static boolean objectsRegistered = false;
     * </pre>
     * The {@link #areObjectsRegistered()} method should return the content of that field, while
     * the {@link #registerObjects(ObjectifyFactory)} method should set that field
     * to {@code true}.
     *
     * @return
     */
    protected abstract boolean areObjectsRegistered();

    /**
     * Override this method to register the objects controlled by this
     * DAO towards objectify.
     * Your implementation should contain calls like:
     * <pre>
     *   ofyFactory.register(MyClass.class);
     * </pre>
     * Make sure you set your static {@code objectsRegistered} field to {@code true}
     * within this method. See {@link #areObjectsRegistered()} for more details.
     *
     * @param ofyFactory The {@link ObjectifyFactory} on which to register objects.
     */
    protected abstract void registerObjects(ObjectifyFactory ofyFactory);

    /**
     * Access the injected factory object.
     *
     * @return The injected {@link ObjectifyFactory}.
     */
    public ObjectifyFactory ofyFactory() {
        return factory;
    }

    /**
     * Access the non-transactional and unique {@link Objectify} object
     * for this DAO. To start a transaction, call {@link #newOfyTransaction()} instead.
     *
     * @return The {@link Objectify} object.
     */
    public Objectify ofy() {
        if (this.lazyOfy == null) {
            this.lazyOfy = factory.begin();
        }

        return this.lazyOfy;
    }

    /**
     * Creates a new transactional {@link Objectify} object.
     *
     * @return A new transactional {@link Objectify} object.
     */
    public Objectify newOfyTransaction() {
        return factory.beginTransaction();
    }

}