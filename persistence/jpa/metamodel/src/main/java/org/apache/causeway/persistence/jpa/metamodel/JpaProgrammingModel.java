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
package org.apache.causeway.persistence.jpa.metamodel;

import org.springframework.stereotype.Component;

import org.apache.causeway.core.metamodel.facetapi.MetaModelRefiner;
import org.apache.causeway.core.metamodel.progmodel.ProgrammingModel;
import org.apache.causeway.core.metamodel.progmodel.ProgrammingModel.Marker;
import org.apache.causeway.persistence.jpa.metamodel.facets.prop.column.BigDecimalFromJpaColumnAnnotationFacetFactory;
import org.apache.causeway.persistence.jpa.metamodel.facets.prop.column.MandatoryFromJpaColumnAnnotationFacetFactory;
import org.apache.causeway.persistence.jpa.metamodel.facets.prop.column.MaxLengthFromJpaColumnAnnotationFacetFactory;
import org.apache.causeway.persistence.jpa.metamodel.facets.prop.transients.JpaTransientAnnotationFacetFactory;
import org.apache.causeway.persistence.jpa.metamodel.object.table.JpaTableAnnotationFacetFactory;

@Component
public class JpaProgrammingModel implements MetaModelRefiner {

    //@Inject private CausewayConfiguration config;

    @Override
    public void refineProgrammingModel(final ProgrammingModel pm) {

        var step = ProgrammingModel.FacetProcessingOrder.A2_AFTER_FALLBACK_DEFAULTS;
        var mmc = pm.getMetaModelContext();

        pm.addFactory(step, new JpaTableAnnotationFacetFactory(mmc), Marker.JPA);
        pm.addFactory(step, new JpaTransientAnnotationFacetFactory(mmc), Marker.JPA);
        pm.addFactory(step, new MandatoryFromJpaColumnAnnotationFacetFactory(mmc), Marker.JPA);
        pm.addFactory(step, new BigDecimalFromJpaColumnAnnotationFacetFactory(mmc), Marker.JPA);
        pm.addFactory(step, new MaxLengthFromJpaColumnAnnotationFacetFactory(mmc), Marker.JPA);

    }

}
