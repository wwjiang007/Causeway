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
package org.apache.causeway.security.spring.authconverters;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.services.user.UserMemento;

import org.jspecify.annotations.NonNull;

/**
 * Applies if {@link Authentication} holds a principal of type {@link UserDetails}.
 */
@Component
@jakarta.annotation.Priority(PriorityPrecedence.LATE - 200)
public class AuthenticationConverterOfUserDetailsPrincipal
extends AuthenticationConverter.Abstract<UserDetails> {

    protected AuthenticationConverterOfUserDetailsPrincipal() {
        super(UserDetails.class);
    }

    @Override
    protected UserMemento convertPrincipal(final @NonNull UserDetails userDetails) {
        return UserMemento.ofNameAndRoleNames(userDetails.getUsername());
    }
}
