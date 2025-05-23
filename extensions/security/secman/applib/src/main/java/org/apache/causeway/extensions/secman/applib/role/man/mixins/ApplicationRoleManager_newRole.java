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
package org.apache.causeway.extensions.secman.applib.role.man.mixins;

import jakarta.inject.Inject;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.MemberSupport;
import org.apache.causeway.applib.annotation.Optionality;
import org.apache.causeway.applib.annotation.Parameter;
import org.apache.causeway.applib.annotation.ParameterLayout;
import org.apache.causeway.applib.annotation.Publishing;
import org.apache.causeway.applib.annotation.SemanticsOf;
import org.apache.causeway.extensions.secman.applib.CausewayModuleExtSecmanApplib;
import org.apache.causeway.extensions.secman.applib.role.dom.ApplicationRole;
import org.apache.causeway.extensions.secman.applib.role.dom.ApplicationRoleRepository;
import org.apache.causeway.extensions.secman.applib.role.man.ApplicationRoleManager;
import org.apache.causeway.extensions.secman.applib.user.man.mixins.ApplicationUserManager_newLocalUser.DomainEvent;

import lombok.RequiredArgsConstructor;

/**
 *
 * @since 2.0 {@index}
 */
@Action(
        commandPublishing = Publishing.NOT_SPECIFIED,
        domainEvent = DomainEvent.class,
        executionPublishing = Publishing.NOT_SPECIFIED,
        semantics = SemanticsOf.IDEMPOTENT
)
@ActionLayout(
        associateWith = "allRoles",
        sequence = "2"
)
@RequiredArgsConstructor
public class ApplicationRoleManager_newRole {

    public static class DomainEvent
            extends CausewayModuleExtSecmanApplib.ActionDomainEvent<ApplicationRoleManager_newRole> {}

    @Inject private ApplicationRoleRepository applicationRoleRepository;

    private final ApplicationRoleManager target;

    @MemberSupport public ApplicationRoleManager act (
            @Parameter(maxLength = ApplicationRole.Name.MAX_LENGTH)
            @ParameterLayout(named="Name", typicalLength= ApplicationRole.Name.TYPICAL_LENGTH)
            final String name,
            @Parameter(maxLength = ApplicationRole.Description.MAX_LENGTH, optionality = Optionality.OPTIONAL)
            @ParameterLayout(named="Description", typicalLength= ApplicationRole.Description.TYPICAL_LENGTH)
            final String description) {
        applicationRoleRepository.newRole(name, description);
        return target;
    }

}
