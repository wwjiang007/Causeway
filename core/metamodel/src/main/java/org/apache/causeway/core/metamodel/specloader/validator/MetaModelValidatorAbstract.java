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
package org.apache.causeway.core.metamodel.specloader.validator;

import java.util.function.Predicate;

import org.apache.causeway.core.metamodel.context.HasMetaModelContext;
import org.apache.causeway.core.metamodel.context.MetaModelContext;
import org.apache.causeway.core.metamodel.spec.ObjectSpecification;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

public abstract class MetaModelValidatorAbstract
implements
    MetaModelValidator,
    HasMetaModelContext {

    @Getter(onMethod_ = {@Override})
    private final @NonNull MetaModelContext metaModelContext;

    @Getter(onMethod_ = {@Override})
    private final @NonNull Predicate<ObjectSpecification> filter;

    protected MetaModelValidatorAbstract(final MetaModelContext metaModelContext) {
        this(metaModelContext, ALL);
    }

    protected MetaModelValidatorAbstract(
            final MetaModelContext metaModelContext,
            final Predicate<ObjectSpecification> filter) {
        this.metaModelContext = metaModelContext;
        this.filter = filter;
    }

    @Override
    public String toString() {
        return getClass().getName();
    }

}
