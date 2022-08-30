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
 *
 */
package org.apache.isis.core.metamodel.facets.actions.action.depdef;

import java.util.Optional;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.commons.internal.base._Optionals;
import org.apache.isis.core.config.IsisConfiguration;
import org.apache.isis.core.config.metamodel.facets.ParameterPolicies;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.SingleValueFacet;

/**
 * Determines how dependent parameter values should be updated,
 * if one of the earlier parameter values is changed.
 * <p>
 * Corresponds to annotating the action method {@link Action#dependentDefaultsPolicy()}.
 *
 * @since 2.0
 */
public interface DependentDefaultsFacet
extends SingleValueFacet<ParameterPolicies.DependentDefaultsPolicy> {

    static Optional<DependentDefaultsFacet> create(
            final Optional<Action> actionsIfAny,
            final IsisConfiguration configuration,
            final FacetHolder holder) {

        final ParameterPolicies.DependentDefaultsPolicy defaultPolicyFromConfig =
                ParameterPolicies.dependentDefaultsPolicy(configuration);

        return _Optionals.orNullable(

        actionsIfAny
        .map(Action::dependentDefaultsPolicy)
        .<DependentDefaultsFacet>map(policy -> {
            switch (policy) {
            case PRESERVE_CHANGES:
                return new DependentDefaultsActionFacetForActionAnnotation(
                        ParameterPolicies.DependentDefaultsPolicy.PRESERVE_CHANGES, holder);
            case UPDATE_DEPENDENT:
                return new DependentDefaultsActionFacetForActionAnnotation(
                        ParameterPolicies.DependentDefaultsPolicy.UPDATE_DEPENDENT, holder);
            case NOT_SPECIFIED:
            case AS_CONFIGURED:
                return new DependentDefaultsActionFacetForActionAnnotation(defaultPolicyFromConfig, holder);
            default:
            }
            throw new IllegalStateException("dependentDefaultsPolicy '" + policy + "' not recognised");
        })
        ,
        () -> new DependentDefaultsActionFacetFromConfiguration(defaultPolicyFromConfig, holder));
    }
}