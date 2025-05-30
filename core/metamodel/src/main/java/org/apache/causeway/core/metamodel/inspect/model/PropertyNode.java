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
package org.apache.causeway.core.metamodel.inspect.model;

import org.apache.causeway.core.metamodel.spec.feature.ObjectMember;
import org.apache.causeway.core.metamodel.spec.feature.OneToOneAssociation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class PropertyNode extends MemberNode {

    private final OneToOneAssociation property;

    @Override
    public String title() {
        return property.getId();
    }

    @Override
    public void putDetails(Details details) {
        details.put("Id", property.getId());
        details.put("Friendly Name", property.getCanonicalFriendlyName());
        details.put("Mixed In", "" + isMixedIn());
        details.put("Element Type", property.getElementType().logicalTypeName());
    }
    
    @Override
    protected ObjectMember member() {
        return property;
    }

}

