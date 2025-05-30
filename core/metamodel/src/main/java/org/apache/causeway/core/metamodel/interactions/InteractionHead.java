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
package org.apache.causeway.core.metamodel.interactions;

import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

import org.apache.causeway.commons.internal.exceptions._Exceptions;
import org.apache.causeway.core.metamodel.object.ManagedObject;
import org.apache.causeway.core.metamodel.object.ManagedObjects;

/**
 * Model that holds the objects involved with the interaction.
 * That is a tuple of {regular-object, (same) regular-object}
 * or {mixee, mixin}, based on whether a regular object or a mixee/mixin pair
 * is represented.
 * @since 2.0
 */
public interface InteractionHead {
    /**
     * The owning object of an interaction.
     */
    ManagedObject owner();

    /**
     * Typically equal to {@code owner}, except for mixins,
     * where {@code target} is the mixin instance.
     */
    ManagedObject target();

    /** Regular case, when owner equals target. (no mixin) */
    public static InteractionHead regular(final ManagedObject owner) {
        return new InteractionHeadRecord(owner, owner);
    }

    /** Mixin case, when target is a mixin for the owner. */
    public static InteractionHead mixin(final @NonNull ManagedObject owner, final @NonNull ManagedObject target) {
        return new InteractionHeadRecord(owner, target);
    }

    /**
     * as used by the domain event subsystem
     * @return optionally the owner (mixee), based on whether the target is a mixin
     */
    default Optional<ManagedObject> getMixee() {
        return Objects.equals(owner(), target())
                ? Optional.empty()
                : Optional.of(owner());
    }

    // -- HELPER

    /**
     * Immutable implementation of {@link InteractionHead} with consistency checks.
     */
    record InteractionHeadRecord(
        ManagedObject owner,
        ManagedObject target) implements InteractionHead {

        // canonical constructor with consistency checks
        public InteractionHeadRecord(
            final ManagedObject owner,
            final ManagedObject target) {
            if(ManagedObjects.isSpecified(owner)
                && owner.objSpec().getBeanSort().isMixin()) {
                throw _Exceptions.unrecoverable("unexpected: owner is a mixin %s", owner);
            }
            if(ManagedObjects.isSpecified(target)
                && target.objSpec().getBeanSort().isMixin()
                && target.getPojo()==null) {
                throw _Exceptions.unrecoverable("target not spec. %s", target);
            }
            this.owner = owner;
            this.target = target;
        }

    }

}