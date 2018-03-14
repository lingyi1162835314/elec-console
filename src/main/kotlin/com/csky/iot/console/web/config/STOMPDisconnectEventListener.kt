/*
 * Copyright (C) 2017 C-SKY Microsystems Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.csky.iot.console.web.config

import com.csky.iot.console.service.ConsoleService
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageType
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Configuration
class STOMPDisconnectEventListener(val consoleService: ConsoleService)
    : ApplicationListener<SessionDisconnectEvent> {

    private val logger = LoggerFactory.getLogger(STOMPDisconnectEventListener::class.java)

    override fun onApplicationEvent(event: SessionDisconnectEvent?) {

        val headers = event!!.message.headers
        val sessionId = headers["simpSessionId"] as String

        val user = event.user
        if (user != null) {
            val cid = user.name
            logger.info("cid {} and  sessionId {} disconnected", cid, sessionId)

            consoleService.disableDevice(cid)
        } else {
            logger.info("sessionId {} disconnected", sessionId)
        }
    }

    private fun createHeaders(sessionId: String): MessageHeaders {
        val headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE)
        headerAccessor.sessionId = sessionId
        headerAccessor.setLeaveMutable(true)
        return headerAccessor.messageHeaders
    }
}