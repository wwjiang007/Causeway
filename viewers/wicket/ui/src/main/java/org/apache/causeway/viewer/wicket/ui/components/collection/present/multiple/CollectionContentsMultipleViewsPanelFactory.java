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
package org.apache.causeway.viewer.wicket.ui.components.collection.present.multiple;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import org.apache.causeway.viewer.commons.model.components.UiComponentType;
import org.apache.causeway.viewer.wicket.model.models.coll.CollectionModel;
import org.apache.causeway.viewer.wicket.ui.ComponentFactory;
import org.apache.causeway.viewer.wicket.ui.ComponentFactoryAbstract;

/**
 * {@link ComponentFactory} for {@link CollectionContentsMultipleViewsPanel}.
 */
public class CollectionContentsMultipleViewsPanelFactory extends ComponentFactoryAbstract {

    public CollectionContentsMultipleViewsPanelFactory() {
        super(UiComponentType.COLLECTION_CONTENTS, CollectionContentsMultipleViewsPanel.class);
    }

    @Override
    public ApplicationAdvice appliesTo(final IModel<?> model) {
        return appliesIf(model instanceof CollectionModel);
    }

    @Override
    public Component createComponent(final String id, final IModel<?> model) {
        final CollectionModel collectionModel = (CollectionModel) model;
        return new CollectionContentsMultipleViewsPanel(id, collectionModel);
    }

}
