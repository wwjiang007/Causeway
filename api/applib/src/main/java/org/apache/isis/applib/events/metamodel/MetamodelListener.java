/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.applib.events.metamodel;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import org.apache.isis.applib.annotation.PriorityPrecedence;
import org.apache.isis.commons.internal.exceptions._Exceptions;

/**
 * Convenience interface to listen on {@link MetamodelEvent}s.
 * <p>
 * Provided as an alternative to directly listening to these events.
 * <p>
 * Using precedence {@link Order} {@link PriorityPrecedence#MIDPOINT}.
 * For fine grained precedence control,
 * services need to directly listen to these events instead.
 *
 * @since 2.0
 */
@FunctionalInterface
public interface MetamodelListener {

    /**
     * Emitted by the framework, once the <i>Metamodel</i> is populated.
     * <p>
     * Most common use-case is to seed database values,
     * right after the <i>Metamodel</i> was loaded.
     */
    void onMetamodelLoaded();

    default void onMetamodelAboutToBeLoaded() {}

    @EventListener(MetamodelEvent.class)
    @Order(PriorityPrecedence.MIDPOINT)
    default void onMetamodelEvent(final MetamodelEvent event) {
        switch(event) {
        case BEFORE_METAMODEL_LOADING:
            onMetamodelAboutToBeLoaded();
            return;
        case AFTER_METAMODEL_LOADED:
            onMetamodelLoaded();
            return;
        default:
            throw _Exceptions.unmatchedCase(event);
        }
    }

}