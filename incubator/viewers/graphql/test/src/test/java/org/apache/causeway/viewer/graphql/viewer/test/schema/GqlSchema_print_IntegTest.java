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
package org.apache.causeway.viewer.graphql.viewer.test.schema;

import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.causeway.commons.io.TextUtils;

import org.apache.causeway.viewer.graphql.viewer.test.CausewayViewerGraphqlTestModuleIntegTestAbstract;

import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import org.apache.causeway.core.config.environment.CausewaySystemEnvironment;
import org.apache.causeway.core.metamodel.specloader.SpecificationLoader;
import org.apache.causeway.viewer.graphql.viewer.source.GraphQlSourceForCauseway;

import static org.apache.causeway.commons.internal.assertions._Assert.assertEquals;
import static org.apache.causeway.commons.internal.assertions._Assert.assertNotNull;
import static org.apache.causeway.commons.internal.assertions._Assert.assertTrue;

import graphql.schema.idl.SchemaPrinter;

import lombok.val;

@Transactional
public class GqlSchema_print_IntegTest extends CausewayViewerGraphqlTestModuleIntegTestAbstract {

    @Inject private CausewaySystemEnvironment causewaySystemEnvironment;
    @Inject private SpecificationLoader specificationLoader;
    @Inject private GraphQlSourceForCauseway graphQlSourceForCauseway;

    @BeforeEach
    void beforeEach() {
        assertNotNull(causewaySystemEnvironment);
        assertNotNull(specificationLoader);
        assertNotNull(graphQlSourceForCauseway);
    }

    @UseReporter({MyWinMergeDiffReporter.class, DiffReporter.class})
    @Test
    void schema() {
        val graphQL = graphQlSourceForCauseway.graphQl();
        val graphQLSchema = graphQL.getGraphQLSchema();

        val printer = new SchemaPrinter();

        val schemaDefinition = printer.print(graphQLSchema);

        Approvals.verify(schemaDefinition, gqlSchemaOptions());
    }

    private Options gqlSchemaOptions() {
        return new Options()
                .withScrubber(this::unixLineEndings)
                .forFile().withExtension(".gql");
    }

    private String unixLineEndings(final String input) {
        return TextUtils.streamLines(input)
                .collect(Collectors.joining("\n"));
    }

}
