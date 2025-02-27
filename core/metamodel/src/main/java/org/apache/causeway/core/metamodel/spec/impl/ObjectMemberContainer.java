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
package org.apache.causeway.core.metamodel.spec.impl;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.causeway.commons.collections.ImmutableEnumSet;
import org.apache.causeway.commons.internal.collections._Sets;
import org.apache.causeway.core.metamodel.facetapi.HasFacetHolder;
import org.apache.causeway.core.metamodel.spec.ActionScope;
import org.apache.causeway.core.metamodel.spec.Hierarchical;
import org.apache.causeway.core.metamodel.spec.feature.MixedIn;
import org.apache.causeway.core.metamodel.spec.feature.ObjectAction;
import org.apache.causeway.core.metamodel.spec.feature.ObjectActionContainer;
import org.apache.causeway.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.causeway.core.metamodel.spec.feature.ObjectAssociationContainer;

/**
 * Responsibility: member lookup and streaming with support for inheritance,
 * based on access to declared members, super-classes and interfaces.
 */
interface ObjectMemberContainer
extends
    HasFacetHolder,
    ObjectActionContainer,
    ObjectAssociationContainer,
    Hierarchical {

    // -- ACTIONS

    @Override
    default Optional<ObjectAction> getAction(
            final String id, final ImmutableEnumSet<ActionScope> scopes, final MixedIn mixedIn) {

        var declaredAction = getDeclaredAction(id, mixedIn); // no inheritance nor type considered

        if(declaredAction.isPresent()) {
            // action found but if its not the right type, stop searching
            if(!scopes.contains(declaredAction.get().getScope())) {
                return Optional.empty();
            }
            return declaredAction;
        }

        return isTypeHierarchyRoot()
                ? Optional.empty() // stop searching
                : superclass().getAction(id, scopes, mixedIn);
    }

    @Override
    default Stream<ObjectAction> streamActions(
            final ImmutableEnumSet<ActionScope> actionTypes,
            final MixedIn mixedIn,
            final Consumer<ObjectAction> onActionOverloaded) {

        var actionStream = isTypeHierarchyRoot()
                ? streamDeclaredActions(actionTypes, mixedIn) // stop going deeper
                : Stream.concat(
                        streamDeclaredActions(actionTypes, mixedIn),
                        superclass().streamActions(actionTypes, mixedIn));

        var actionSignatures = _Sets.<String>newHashSet();
        var actionIds = _Sets.<String>newHashSet();

        return actionStream

            // as of contributing super-classes same actions might appear more than once (overriding)
            .filter(action->{
                if(action.isMixedIn()) {
                    return true; // do not filter mixedIn actions based on signature
                }
                var isUnique = actionSignatures
                        .add(action.getFeatureIdentifier().getMemberNameAndParameterClassNamesIdentityString());
                return isUnique;
            })

            // ensure we don't emit duplicates
            .filter(action->{
                var isUnique = actionIds.add(action.getId());
                if(!isUnique) {
                    onActionOverloaded.accept(action);
                }
                return isUnique;
            });
    }

    // -- ASSOCIATIONS

    @Override
    default Optional<ObjectAssociation> getAssociation(final String id, final MixedIn mixedIn) {

        var declaredAssociation = getDeclaredAssociation(id, mixedIn); // no inheritance considered

        if(declaredAssociation.isPresent()) return declaredAssociation;

        return isTypeHierarchyRoot()
               ? Optional.empty() // stop searching
               : superclass().getAssociation(id, mixedIn);
    }

    @Override
    default Stream<ObjectAssociation> streamAssociations(final MixedIn mixedIn) {

        if(isTypeHierarchyRoot()) return streamDeclaredAssociations(mixedIn); // stop going deeper

        var ids = _Sets.<String>newHashSet();

        return Stream.concat(
                streamDeclaredAssociations(mixedIn),
                superclass().streamAssociations(mixedIn)
            )
            .filter(association->ids.add(association.getId())); // ensure we don't emit duplicates
    }

}
