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
package org.apache.causeway.security.spring.webmodule;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.services.inject.ServiceInjector;
import org.apache.causeway.commons.collections.Can;
import org.apache.causeway.core.webapp.modules.WebModuleAbstract;
import org.apache.causeway.security.spring.CausewayModuleSecuritySpring;

import lombok.Getter;

/**
 * WebModule to enable support for Spring Security.
 *
 * @since 2.0 {@index}
 */
@Service
@Named(WebModuleSpringSecurity.LOGICAL_TYPE_NAME)
@jakarta.annotation.Priority(PriorityPrecedence.FIRST + 100)
@Qualifier("Spring")
public final class WebModuleSpringSecurity extends WebModuleAbstract {

    public static final String LOGICAL_TYPE_NAME = CausewayModuleSecuritySpring.NAMESPACE + ".WebModuleSpringSecurity";

    @Getter
    private final String name = "Spring Security Integration";

    @Inject
    public WebModuleSpringSecurity(final ServiceInjector serviceInjector) {
        super(serviceInjector);
    }

    @Override
    public Can<ServletContextListener> init(final ServletContext ctx) throws ServletException {

        registerFilter(ctx, "SpringSecurityFilter", SpringSecurityFilter.class)
            .ifPresent(filterReg -> {
                filterReg.addMappingForUrlPatterns(
                        null,
                        true, // filter is not forced first
                        "/*");

            });

        return Can.empty(); // registers no listeners
    }

}
