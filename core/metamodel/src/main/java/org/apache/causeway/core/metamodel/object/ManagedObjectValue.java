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
package org.apache.causeway.core.metamodel.object;

import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

import org.apache.causeway.applib.services.bookmark.Bookmark;
import org.apache.causeway.commons.internal.assertions._Assert;
import org.apache.causeway.commons.internal.base._Casts;
import org.apache.causeway.commons.internal.base._Lazy;
import org.apache.causeway.core.metamodel.facets.object.title.TitleRenderRequest;
import org.apache.causeway.core.metamodel.facets.object.value.ValueSerializer.Format;
import org.apache.causeway.core.metamodel.objectmanager.memento.ObjectMemento;
import org.apache.causeway.core.metamodel.spec.ObjectSpecification;

/**
 * (package private) specialization corresponding to {@link Specialization#VALUE}
 * @see ManagedObject.Specialization#VALUE
 */
record ManagedObjectValue(
    @NonNull ObjectSpecification objSpec,
    @NonNull Object pojo,
    @NonNull _Lazy<Bookmark> bookmarkLazy)
implements ManagedObject {

    ManagedObjectValue(
            final ObjectSpecification objSpec,
            final Object pojo) {
        this(objSpec, pojo, _Lazy.threadSafe(()->createBookmark(objSpec, pojo)));
        _Assert.assertTrue(objSpec.isValue());
        specialization().assertCompliance(objSpec, pojo);
    }

    @Override
    public Optional<Bookmark> getBookmark() {
        return Optional.of(bookmarkLazy.get());
    }

    @Override
    public boolean isBookmarkMemoized() {
        return bookmarkLazy.isMemoized();
    }

    @Override
    public Optional<ObjectMemento> getMemento() {
        return ObjectMemento.singular(this);
    }

    @Override
    public String getTitle() {
        return _InternalTitleUtil.titleString(
            TitleRenderRequest.forObject(this));
    }

    @Override
    public Specialization specialization() {
        return ManagedObject.Specialization.VALUE;
    }

    @Override
    public Object getPojo() {
        return pojo;
    }

    @Override
    public final boolean equals(final Object obj) {
        return obj instanceof ManagedObjectValue other
            ? Objects.equals(pojo, other.pojo)
            : false;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(pojo);
    }

    @Override
    public final String toString() {
        return "ManagedObjectValue[pojo=%s]".formatted(pojo);
    }

    // -- HELPER

    private static Bookmark createBookmark(final ObjectSpecification objSpec, final Object pojo) {
        String urlSafeIdentifier = objSpec.valueFacetElseFail()
            .enstring(Format.URL_SAFE, _Casts.uncheckedCast(pojo));
        return Bookmark.forLogicalTypeAndIdentifier(objSpec.logicalType(), urlSafeIdentifier);
    }

}