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
package org.apache.causeway.viewer.graphql.model.domain;

import org.apache.causeway.core.metamodel.object.ManagedObject;
import org.apache.causeway.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.causeway.viewer.graphql.model.context.Context;
import org.apache.causeway.viewer.graphql.model.fetcher.BookmarkedPojo;
import org.apache.causeway.viewer.graphql.model.mmproviders.ObjectAssociationProvider;
import org.apache.causeway.viewer.graphql.model.mmproviders.ObjectSpecificationProvider;

import lombok.val;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLOutputType;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

public abstract class GqlvAssociationGet<T extends ObjectAssociation> {

    final Holder<T> holder;
    final Context context;
    final GraphQLFieldDefinition field;

    public GqlvAssociationGet(
            final Holder<T> holder,
            final Context context) {
        this.holder = holder;
        this.context = context;
        this.field = fieldDefinition(holder);
    }

    GraphQLFieldDefinition fieldDefinition(final Holder<T> holder) {

        GraphQLFieldDefinition fieldDefinition = null;
        GraphQLOutputType type = outputTypeFor(holder);
        if (type != null) {
            val fieldBuilder = newFieldDefinition()
                    .name("get")
                    .type(type);
            fieldDefinition = fieldBuilder.build();

            holder.addField(fieldDefinition);
        }
        return fieldDefinition;
    }

    abstract GraphQLOutputType outputTypeFor(Holder<T> holder);

    void addDataFetcher() {

        val association = holder.getObjectAssociation();
        val fieldObjectSpecification = association.getElementType();
        val beanSort = fieldObjectSpecification.getBeanSort();

        switch (beanSort) {

            case VALUE:
            case VIEW_MODEL:
            case ENTITY:

                context.codeRegistryBuilder.dataFetcher(
                        holder.coordinatesFor(field),
                        this::get);

                break;

        }
    }

    Object get(final DataFetchingEnvironment dataFetchingEnvironment) {

        // TODO: introduce evaluator
        val sourcePojo = BookmarkedPojo.sourceFrom(dataFetchingEnvironment);

        val sourcePojoClass = sourcePojo.getClass();
        val objectSpecification = context.specificationLoader.loadSpecification(sourcePojoClass);
        if (objectSpecification == null) {
            // not expected
            return null;
        }

        val association = holder.getObjectAssociation();
        val managedObject = ManagedObject.adaptSingular(objectSpecification, sourcePojo);
        val resultManagedObject = association.get(managedObject);

        return resultManagedObject != null
                ? resultManagedObject.getPojo()
                : null;
    }

    public interface Holder<T extends ObjectAssociation>
            extends GqlvHolder,
            ObjectSpecificationProvider,
            ObjectAssociationProvider<T> {

    }
}