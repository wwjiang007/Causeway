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
package org.apache.causeway.viewer.restfulobjects.viewer.resources;

import java.io.InputStream;
import java.util.Map;
import java.util.function.UnaryOperator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Providers;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.causeway.applib.annotation.Where;
import org.apache.causeway.applib.services.bookmark.Bookmark;
import org.apache.causeway.commons.internal.base._Strings;
import org.apache.causeway.commons.internal.functions._Predicates;
import org.apache.causeway.commons.io.TextUtils;
import org.apache.causeway.commons.io.UrlUtils;
import org.apache.causeway.core.config.viewer.web.WebAppContextPath;
import org.apache.causeway.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.causeway.core.metamodel.context.HasMetaModelContext;
import org.apache.causeway.core.metamodel.context.MetaModelContext;
import org.apache.causeway.core.metamodel.object.ManagedObject;
import org.apache.causeway.core.metamodel.object.ManagedObjects;
import org.apache.causeway.viewer.restfulobjects.applib.RepresentationType;
import org.apache.causeway.viewer.restfulobjects.applib.RestfulResponse.HttpStatusCode;
import org.apache.causeway.viewer.restfulobjects.rendering.RestfulObjectsApplicationException;
import org.apache.causeway.viewer.restfulobjects.rendering.UrlDecoderUtils;
import org.apache.causeway.viewer.restfulobjects.rendering.service.RepresentationService;
import org.apache.causeway.viewer.restfulobjects.rendering.util.RequestParams;
import org.apache.causeway.viewer.restfulobjects.viewer.context.ResourceContext;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

public abstract class ResourceAbstract
implements HasMetaModelContext {

    @Getter(onMethod_={@Override})
    @Autowired protected MetaModelContext metaModelContext;
    @Autowired protected WebAppContextPath webAppContextPath;

    @Context HttpHeaders httpHeaders;
    @Context UriInfo uriInfo;
    @Context Request request;
    @Context HttpServletRequest httpServletRequest;
    @Context HttpServletResponse httpServletResponse;
    @Context SecurityContext securityContext;
    @Context Providers providers;

    protected ResourceAbstract() {
    }

    // -- FACTORIES

    protected ResourceContext createResourceContext(
            final RepresentationType representationType,
            final Where where,
            final RepresentationService.Intent intent) {

        return createResourceContext(new ResourceDescriptor(representationType, where, intent));
    }

    protected ResourceContext createResourceContext(final ResourceDescriptor resourceDescriptor) {
        String queryStringIfAny = getUrlDecodedQueryStringIfAny();
        return createResourceContext(resourceDescriptor, RequestParams.ofQueryString(queryStringIfAny));
    }

    protected ResourceContext createResourceContext(
            final ResourceDescriptor resourceDescriptor,
            final InputStream arguments) {

        var urlDecodedQueryString = RequestParams.ofRequestBody(arguments);
        return createResourceContext(resourceDescriptor, urlDecodedQueryString);
    }

    protected ResourceContext createResourceContext(
            final ResourceDescriptor resourceDescriptor,
            final RequestParams requestParams) {

        if (!getInteractionService().isInInteraction()) {
            throw RestfulObjectsApplicationException.create(HttpStatusCode.UNAUTHORIZED);
        }

        // eg. http://localhost:8080/ctx/restful/
        final String restfulAbsoluteBase = getConfiguration().getViewer().getRestfulobjects().getBaseUri()
                                    .orElseGet(()->uriInfo.getBaseUri().toString());

        // eg. /ctx/restful/
        var restfulRelativeBase = uriInfo.getBaseUri().getRawPath();

        // eg. http://localhost:8080/
        var serverAbsoluteBase =
                _Strings
                .suffix(TextUtils.cutter(restfulAbsoluteBase)
                        .keepAfterLast(restfulRelativeBase)
                        .getValue(),
                "/");

        // eg. http://localhost:8080/ctx/
        var applicationAbsoluteBase = _Strings
                .suffix(webAppContextPath.appendContextPath(serverAbsoluteBase), "/");

        return resourceContext(
                resourceDescriptor,
                applicationAbsoluteBase,
                restfulAbsoluteBase,
                requestParams,
                httpServletRequest.getParameterMap());
    }

    public ResourceContext resourceContextForTesting(
            final ResourceDescriptor resourceDescriptor,
            final Map<String, String[]> requestParams) {

        return resourceContext(
                resourceDescriptor, "", "/restful", /*urlUnencodedQueryString*/ null, requestParams);
    }

    // -- CAUSEWAY INTEGRATION

    protected ManagedObject getObjectAdapterElseThrowNotFound(
            final String domainType,
            final String instanceIdEncoded,
            final @NonNull UnaryOperator<RestfulObjectsApplicationException> onRoException) {
        final String instanceIdDecoded = UrlDecoderUtils.urlDecode(instanceIdEncoded);

        var bookmark = Bookmark.forLogicalTypeNameAndIdentifier(domainType, instanceIdDecoded);
        return metaModelContext.getObjectManager().loadObject(bookmark) // might return ManagedObject.EMPTY
                .filter(_Predicates.not(ManagedObjects::isNullOrUnspecifiedOrEmpty))
                .orElseThrow(()->onRoException.apply(
                        RestfulObjectsApplicationException
                                .createWithMessage(HttpStatusCode.NOT_FOUND,
                                        "Could not determine adapter for bookmark: '%s'",
                                        bookmark)));
    }

    // -- HELPER

    private String getUrlDecodedQueryStringIfAny() {
        final String queryStringIfAny = httpServletRequest.getQueryString();
        return UrlUtils.urlDecodeUtf8(queryStringIfAny);
    }

    private ResourceContext resourceContext(
            final ResourceDescriptor resourceDescriptor,
            final String applicationAbsoluteBase,
            final String restfulAbsoluteBase,
            final RequestParams urlUnencodedQueryString,
            final Map<String, String[]> requestParams) {

        return new ResourceContext(
                resourceDescriptor,
                httpHeaders, providers, request,
                applicationAbsoluteBase,
                restfulAbsoluteBase,
                urlUnencodedQueryString,
                httpServletRequest, httpServletResponse,
                securityContext,
                metaModelContext,
                InteractionInitiatedBy.USER,
                requestParams);
    }

}
