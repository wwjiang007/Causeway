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
package org.apache.isis.core.metamodel.facets.properties.validating.method;

import javax.inject.Inject;

import org.apache.isis.core.config.progmodel.ProgrammingModelConstants.MemberSupportPrefix;
import org.apache.isis.core.metamodel.context.MetaModelContext;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facets.members.support.MemberSupportFacetFactoryAbstract;
import org.apache.isis.core.metamodel.methods.MethodFinderOptions;

import lombok.val;

public class PropertyValidateFacetViaMethodFactory
extends MemberSupportFacetFactoryAbstract  {

    @Inject
    public PropertyValidateFacetViaMethodFactory(final MetaModelContext mmc) {
        super(mmc, FeatureType.PROPERTIES_ONLY, MemberSupportPrefix.VALIDATE);
    }

    @Override
    protected void search(
            final ProcessMethodContext processMethodContext,
            final MethodFinderOptions methodFinderOptions) {

        val getterMethod = processMethodContext.getMethod();
        val argType = getterMethod.getReturnType();

        methodFinderOptions
        .streamMethodsMatchingSignature(new Class[] { argType })
        .peek(processMethodContext::removeMethod)
        .forEach(validateMethod->{
            addFacet(
                    new PropertyValidateFacetViaMethod(
                            validateMethod, processMethodContext.getFacetHolder()));
        });

    }

}
