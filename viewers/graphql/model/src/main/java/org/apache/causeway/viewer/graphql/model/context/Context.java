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
package org.apache.causeway.viewer.graphql.model.context;

import graphql.schema.GraphQLCodeRegistry;

import org.apache.causeway.applib.services.bookmark.BookmarkService;
import org.apache.causeway.commons.collections.ImmutableEnumSet;
import org.apache.causeway.core.config.CausewayConfiguration;
import org.apache.causeway.core.config.environment.CausewaySystemEnvironment;
import org.apache.causeway.core.metamodel.spec.ActionScope;
import org.apache.causeway.core.metamodel.specloader.SpecificationLoader;
import org.apache.causeway.viewer.graphql.applib.types.TypeMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Context {
    public final GraphQLCodeRegistry.Builder codeRegistryBuilder;
    public final BookmarkService bookmarkService;
    public final SpecificationLoader specificationLoader;
    public final TypeMapper typeMapper;
    public final CausewayConfiguration causewayConfiguration;
    public final CausewaySystemEnvironment causewaySystemEnvironment;

    public ImmutableEnumSet<ActionScope> getActionScope() {
        return causewaySystemEnvironment.getDeploymentType().isProduction()
                ? ActionScope.PRODUCTION_ONLY
                : ActionScope.ANY;
    }

}