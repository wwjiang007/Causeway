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
package org.apache.isis.core.runtimeservices.publish;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.apache.isis.applib.annotation.InteractionScope;
import org.apache.isis.applib.annotation.PriorityPrecedence;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.core.metamodel.facets.object.callbacks.CallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.CreatedCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.CreatedLifecycleEventFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.LoadedCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.LoadedLifecycleEventFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.PersistedCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.PersistedLifecycleEventFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.PersistingCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.PersistingLifecycleEventFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.RemovingCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.RemovingLifecycleEventFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.UpdatedCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.UpdatedLifecycleEventFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.UpdatingCallbackFacet;
import org.apache.isis.core.metamodel.facets.object.callbacks.UpdatingLifecycleEventFacet;
import org.apache.isis.core.metamodel.spec.ManagedObject;
import org.apache.isis.core.runtimeservices.IsisModuleCoreRuntimeServices;
import org.apache.isis.core.transaction.changetracking.PersistenceCallbackHandlerAbstract;
import org.apache.isis.core.transaction.changetracking.events.PostStoreEvent;
import org.apache.isis.core.transaction.changetracking.events.PreStoreEvent;

/**
 * Calls lifecycle callbacks for entities, ensuring that any given entity is only ever called once.
 * @since 2.0 {@index}
 */
@Component
@Named(IsisModuleCoreRuntimeServices.NAMESPACE + ".LifecycleCallbackNotifier")
@Priority(PriorityPrecedence.EARLY)
@Qualifier("Default")
@InteractionScope
//@Log4j2
public class LifecycleCallbackNotifier extends PersistenceCallbackHandlerAbstract {

    private final Set<ManagedObject> postCreated = new LinkedHashSet<>();
    private final Set<ManagedObject> postLoaded = new LinkedHashSet<>();
    private final Set<ManagedObject> prePersisted = new LinkedHashSet<>();
    private final Set<ManagedObject> postPersisted = new LinkedHashSet<>();
    private final Set<ManagedObject> preUpdated = new LinkedHashSet<>();
    private final Set<ManagedObject> postUpdated = new LinkedHashSet<>();
    private final Set<ManagedObject> preRemoved = new LinkedHashSet<>();

    @Inject
    public LifecycleCallbackNotifier(EventBusService eventBusService) {
        super(eventBusService);
    }

    public void postCreate(ManagedObject entity) {
        notify(entity,
                postCreated,
                e -> {
                    CallbackFacet.callCallback(entity, CreatedCallbackFacet.class);
                    postLifecycleEventIfRequired(entity, CreatedLifecycleEventFacet.class);
                });
    }

    public void postLoad(ManagedObject entity) {
        notify(entity,
                postLoaded,
                e -> {
                    CallbackFacet.callCallback(entity, LoadedCallbackFacet.class);
                    postLifecycleEventIfRequired(entity, LoadedLifecycleEventFacet.class);
                });
    }

    public void prePersist(ManagedObject entity) {
        notify(entity,
                prePersisted,
                e -> {
                    eventBusService.post(PreStoreEvent.of(entity.getPojo()));
                    CallbackFacet.callCallback(entity, PersistingCallbackFacet.class);
                    postLifecycleEventIfRequired(entity, PersistingLifecycleEventFacet.class);
                });
    }

    public void postPersist(ManagedObject entity) {
        notify(entity,
                postPersisted,
                e -> {
                    eventBusService.post(PostStoreEvent.of(entity.getPojo()));
                    CallbackFacet.callCallback(entity, PersistedCallbackFacet.class);
                    postLifecycleEventIfRequired(entity, PersistedLifecycleEventFacet.class);
                });
    }

    public void preUpdate(ManagedObject entity) {
        notify(entity,
                preUpdated,
                e -> {
                    eventBusService.post(PreStoreEvent.of(entity.getPojo()));
                    CallbackFacet.callCallback(entity, UpdatingCallbackFacet.class);
                    postLifecycleEventIfRequired(entity, UpdatingLifecycleEventFacet.class);
                });
    }

    public void postUpdate(ManagedObject entity) {
        notify(entity,
                postUpdated,
                e -> {
                    CallbackFacet.callCallback(entity, UpdatedCallbackFacet.class);
                    postLifecycleEventIfRequired(entity, UpdatedLifecycleEventFacet.class);
                });
    }

    public void preRemove(ManagedObject entity) {
        notify(entity,
                preRemoved,
                e -> {
                    CallbackFacet.callCallback(entity, RemovingCallbackFacet.class);
                    postLifecycleEventIfRequired(entity, RemovingLifecycleEventFacet.class);
                });
    }

    private static void notify(ManagedObject entity, Set<ManagedObject> notified, Consumer<ManagedObject> notify) {
        Optional.of(entity)
                .filter(x -> !notified.contains(x))
                .ifPresent(x -> {
                    notify.accept(entity);
                    notified.add(x);
                });
    }

}
