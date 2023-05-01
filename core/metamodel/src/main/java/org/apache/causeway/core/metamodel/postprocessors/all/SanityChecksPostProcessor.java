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
package org.apache.causeway.core.metamodel.postprocessors.all;

import javax.inject.Inject;

import org.apache.causeway.core.config.progmodel.ProgrammingModelConstants;
import org.apache.causeway.core.metamodel.context.MetaModelContext;
import org.apache.causeway.core.metamodel.facetapi.FacetHolder;
import org.apache.causeway.core.metamodel.postprocessors.ObjectSpecificationPostProcessorAbstract;
import org.apache.causeway.core.metamodel.spec.ObjectSpecification;
import org.apache.causeway.core.metamodel.spec.feature.ObjectAction;
import org.apache.causeway.core.metamodel.spec.feature.ObjectActionParameter;
import org.apache.causeway.core.metamodel.spec.feature.OneToManyAssociation;
import org.apache.causeway.core.metamodel.spec.feature.OneToOneAssociation;
import org.apache.causeway.core.metamodel.specloader.validator.ValidationFailure;

/**
 * Checks various preconditions for a sane meta-model.
 * <ul>
 * <li>Guard against members that contribute vetoed or managed types.
 * Those are not allowed as member/return/param.</li>
 * </ul>
 */
public class SanityChecksPostProcessor
extends ObjectSpecificationPostProcessorAbstract {

    @Inject
    public SanityChecksPostProcessor(final MetaModelContext mmc) {
        super(mmc);
    }

    @Override
    public void postProcessParameter(final ObjectSpecification objectSpecification, final ObjectAction objectAction, final ObjectActionParameter parameter) {
        checkElementType(parameter, objectSpecification, parameter.getElementType());
    }

    @Override
    public void postProcessAction(final ObjectSpecification objectSpecification, final ObjectAction objectAction) {
        checkElementType(objectAction, objectSpecification, objectAction.getElementType());
    }

    @Override
    public void postProcessProperty(final ObjectSpecification objectSpecification, final OneToOneAssociation prop) {
        checkElementType(prop, objectSpecification, prop.getElementType());
    }

    @Override
    public void postProcessCollection(final ObjectSpecification objectSpecification, final OneToManyAssociation coll) {
        checkElementType(coll, objectSpecification, coll.getElementType());
    }

    // -- HELPER

    private void checkElementType(
            final FacetHolder facetHolder,
            final ObjectSpecification declaringType,
            final ObjectSpecification elementType) {

        if(elementType == null
                || elementType.getBeanSort().isManagedBeanAny()
                || elementType.getBeanSort().isMixin()
                || elementType.getBeanSort().isVetoed()) {

            ValidationFailure.raiseFormatted(facetHolder,
                    ProgrammingModelConstants.Violation.VETOED_OR_MANAGED_TYPE_NOT_ALLOWED_TO_ENTER_METAMODEL
                        .builder()
                        .addVariable("type", declaringType.fqcn())
                        .addVariable("elementType", ""+elementType)
                        .buildMessage());
        }
    }

}