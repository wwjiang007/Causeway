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
package org.apache.causeway.extensions.secman.applib.user.contributions;

import jakarta.inject.Inject;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.MemberSupport;
import org.apache.causeway.applib.annotation.Publishing;
import org.apache.causeway.applib.annotation.SemanticsOf;
import org.apache.causeway.applib.mixins.security.HasUsername;
import org.apache.causeway.applib.services.i18n.TranslatableString;
import org.apache.causeway.extensions.secman.applib.CausewayModuleExtSecmanApplib;
import org.apache.causeway.extensions.secman.applib.user.dom.ApplicationUser;
import org.apache.causeway.extensions.secman.applib.user.dom.ApplicationUserRepository;

import lombok.RequiredArgsConstructor;

/**
 *
 * @since 2.0 {@index}
 */
@Action(
        commandPublishing = Publishing.DISABLED,
        semantics = SemanticsOf.SAFE,
        executionPublishing = Publishing.DISABLED,
        domainEvent = HasUsername_associatedUser.ActionDomainEvent.class
)
@ActionLayout(
        associateWith = "user", // associate with a 'User' property (if any)
        sequence = "1"
)
@RequiredArgsConstructor
public class HasUsername_associatedUser {

    @Inject private ApplicationUserRepository applicationUserRepository;

    private final HasUsername target;

    public static class ActionDomainEvent extends CausewayModuleExtSecmanApplib.ActionDomainEvent<HasUsername_associatedUser> {}

    @MemberSupport public ApplicationUser act() {
        if (target == null || target.getUsername() == null) {
            return null;
        }
        return applicationUserRepository.findByUsername(target.getUsername()).orElse(null);
    }

    @MemberSupport public boolean hideAct() { return target instanceof ApplicationUser; }
    @MemberSupport public TranslatableString disableAct() {
        return target == null || target.getUsername() == null ? TranslatableString.tr("No username") : null;
    }

}
