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
package org.apache.causeway.extensions.secman.applib.role.dom.mixins;

import java.util.Collection;

import jakarta.inject.Inject;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.Domain;
import org.apache.causeway.applib.annotation.MemberSupport;
import org.apache.causeway.applib.annotation.Publishing;
import org.apache.causeway.applib.annotation.SemanticsOf;
import org.apache.causeway.applib.services.message.MessageService;
import org.apache.causeway.commons.internal.base._NullSafe;
import org.apache.causeway.extensions.secman.applib.CausewayModuleExtSecmanApplib;
import org.apache.causeway.extensions.secman.applib.role.dom.ApplicationRole;
import org.apache.causeway.extensions.secman.applib.role.dom.ApplicationRoleRepository;
import org.apache.causeway.extensions.secman.applib.role.dom.mixins.ApplicationRole_removeUsers.DomainEvent;
import org.apache.causeway.extensions.secman.applib.user.dom.ApplicationUser;
import org.apache.causeway.extensions.secman.applib.user.dom.ApplicationUserRepository;

import lombok.RequiredArgsConstructor;

/**
 *
 * @since 2.0 {@index}
 */
@Action(
        choicesFrom = "users",
        commandPublishing = Publishing.NOT_SPECIFIED,
        domainEvent = DomainEvent.class,
        executionPublishing = Publishing.NOT_SPECIFIED,
        semantics = SemanticsOf.IDEMPOTENT
)
@ActionLayout(
        associateWith = "users",
        named = "Remove",
        sequence = "2"
)
@RequiredArgsConstructor
public class ApplicationRole_removeUsers {

    public static class DomainEvent
            extends CausewayModuleExtSecmanApplib.ActionDomainEvent<ApplicationRole_removeUsers> {}

    @Inject private MessageService messageService;
    @Inject private ApplicationRoleRepository applicationRoleRepository;
    @Inject private ApplicationUserRepository applicationUserRepository;

    private final ApplicationRole target;

    @MemberSupport public ApplicationRole act(Collection<ApplicationUser> users) {

        _NullSafe.stream(users)
        .filter(this::canRemove)
        .forEach(user->applicationRoleRepository.removeRoleFromUser(target, user));

        return target;
    }

    @Domain.Exclude
    boolean canRemove(ApplicationUser applicationUser) {
        if(applicationUserRepository.isAdminUser(applicationUser)
                && applicationRoleRepository.isAdminRole(target)) {
            messageService.warnUser("Cannot remove admin user from the admin role.");
            return false;
        }
        return true;
    }

}
