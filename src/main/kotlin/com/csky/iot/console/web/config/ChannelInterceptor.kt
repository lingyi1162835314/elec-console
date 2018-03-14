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

import com.csky.iot.utils.LogUtil
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptorAdapter
import org.springframework.messaging.support.MessageHeaderAccessor
import java.security.Principal

class ChannelInterceptor : ChannelInterceptorAdapter() {

    override fun preSend(message: Message<*>?, channel: MessageChannel?): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        if (StompCommand.CONNECT == accessor.command) {
            val cid = accessor.getNativeHeader("cid")[0]
            LogUtil.i(javaClass, "[WebSocket] [cid]:" + cid)
          //  val cid = userId
            accessor.user = CustomerPrincipal(cid)
        }

        return message
    }
}

internal class CustomerPrincipal(val cid: String): Principal {
    override fun getName(): String {
        return cid
    }
}