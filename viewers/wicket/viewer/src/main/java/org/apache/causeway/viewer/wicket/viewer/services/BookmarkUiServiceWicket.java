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
package org.apache.causeway.viewer.wicket.viewer.services;

import jakarta.inject.Named;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.services.bookmarkui.BookmarkUiService;
import org.apache.causeway.viewer.wicket.viewer.CausewayModuleViewerWicketViewer;
import org.apache.causeway.viewer.wicket.viewer.integration.AuthenticatedWebSessionForCauseway;

/**
 * Implementation of {@link BookmarkUiService}.
 *
 * @since 1.x revised for 2.0 {@index}
 */
@Service
@Named(BookmarkUiServiceWicket.LOGICAL_TYPE_NAME)
@jakarta.annotation.Priority(PriorityPrecedence.EARLY)
@Qualifier("Wicket")
public class BookmarkUiServiceWicket implements BookmarkUiService {

    public static final String LOGICAL_TYPE_NAME =
            CausewayModuleViewerWicketViewer.NAMESPACE + ".BookmarkUiServiceWicket";

    @Override
    public void clear() {
        var session = AuthenticatedWebSessionForCauseway.get();
        if (session == null) {
            return;
        }
        session.getBreadcrumbModel().clear();
        session.getBookmarkedPagesModel().clear();
    }
}
