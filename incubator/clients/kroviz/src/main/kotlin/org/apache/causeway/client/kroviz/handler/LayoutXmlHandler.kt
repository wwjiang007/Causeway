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
package org.apache.causeway.client.kroviz.handler

import org.apache.causeway.client.kroviz.to.TransferObject
import org.apache.causeway.client.kroviz.to.bs.GridBs
import org.apache.causeway.client.kroviz.utils.UrlUtils
import org.apache.causeway.client.kroviz.utils.XmlHelper

class LayoutXmlHandler : BaseHandler() {

    override fun canHandle(response: String): Boolean {
        val isXmlLayout = XmlHelper.isXml(response)
                && UrlUtils.isLayout(logEntry.url)
        if (isXmlLayout) {
            return super.canHandle(response)
        }
        return false
    }

    override fun parse(response: String): TransferObject {
        val doc = XmlHelper.parseXml(response)
        return GridBs(doc)
    }

}
