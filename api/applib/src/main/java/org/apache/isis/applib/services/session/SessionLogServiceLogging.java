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
package org.apache.isis.applib.services.session;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Priority;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.apache.isis.applib.annotation.PriorityPrecedence;

import lombok.val;
import lombok.extern.log4j.Log4j2;

/**
 * @since 1.x {@index}
 */
@Service
@Named("isis.applib.SessionLoggingServiceLogging")
@Priority(PriorityPrecedence.LATE)
@Qualifier("logging")
@Log4j2
public class SessionLogServiceLogging implements SessionLogService {

    @Override
    public void log(
            final Type type,
            final String username,
            final Date date,
            final CausedBy causedBy,
            final UUID sessionGuid,
            final String httpSessionId) {

        if(log.isDebugEnabled()) {

            val msg = String.format(
                    "User '%s' with sessionGuid '%s' (httpSessionId '%s') has logged %s at '%s'.%s",
                    username,
                    sessionGuid,
                    httpSessionId,
                    type == Type.LOGIN ? "in" : "out",
                    date,
                    causedBy == CausedBy.SESSION_EXPIRATION ? " (session expiration)" : ""
            );
            log.debug(msg);
        }
    }
}