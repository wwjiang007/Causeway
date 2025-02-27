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
package org.apache.causeway.viewer.wicket.model.tableoption;

import java.util.function.Predicate;

import lombok.RequiredArgsConstructor;

/**
 * Various choices for the select-action dropdown.
 */
@RequiredArgsConstructor
public enum SelectOperationChoiceKey {
    SEL_ALL("select ALL (filtered) rows of this table", "solid check-double"),
    CLEAR("CLEAR selection", "solid xmark"),
    PAGE_SEL("select ALL rows of this PAGE", "regular square-check"),
    PAGE_UNSEL("unselect ALL rows of this PAGE", "regular rectangle-xmark");

    public final String englishTitle;
    public final String cssClass;

    /**
     * Associated operation should only be available when table is showing pages.
     */
    public boolean isPageSpecific() {
        return this==PAGE_SEL
            || this == PAGE_UNSEL;
    }

    public static Predicate<SelectOperationChoiceKey> isAvailableWhen(final boolean isTablePaged) {
        return (final SelectOperationChoiceKey key)->isTablePaged
            ? true
            : !key.isPageSpecific();
    }

}