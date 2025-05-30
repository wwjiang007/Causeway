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
package org.apache.causeway.core.runtimeservices.eventbus;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.services.eventbus.EventBusService;
import org.apache.causeway.core.runtimeservices.CausewayModuleCoreRuntimeServices;

/**
 * Default implementation of {@link EventBusService}.
 *
 * @since 2.0 {@index}
 */
@Service
@Named(CausewayModuleCoreRuntimeServices.NAMESPACE + ".EventBusServiceSpring")
@Priority(PriorityPrecedence.MIDPOINT)
@Qualifier("Spring")
public class EventBusServiceSpring implements EventBusService {

    @Inject private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void post(Object event) {
        applicationEventPublisher.publishEvent(event);
    }

}
